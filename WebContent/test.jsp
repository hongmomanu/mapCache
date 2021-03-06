<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<meta name="apple-mobile-web-app-capable" content="yes">
<title>OpenLayers MapQuest Demo</title>
<script src="http://www.openlayers.cn/olapi/OpenLayers.js"></script>
<link rel="stylesheet" href="http://www.openlayers.cn/olapi/theme/default/style.css"type="text/css">
<style type="text/css">
    .olImageLoadError
    {
                /*ol2.12   onImageLoadError no longer exists,  replace */
            display: none !important;

    }

</style>


<script type="text/javascript">
var map,ctrlLyrSwitcher;
function init(){
    map = new OpenLayers.Map({
        div: "map",
        projection: "EPSG:900913",//EPSG:后不能有空格
        displayProjection: new OpenLayers.Projection("EPSG:4326"),

        numZoomLevels:20,
        layers: [

            new OpenLayers.Layer.XYZ(

            "google",
                    [

            "http://khm0.google.com/kh/v=123&x=${x}&y=${y}&z=${z}",
            "http://khm1.google.com/kh/v=123&x=${x}&y=${y}&z=${z}",
            "http://khm2.google.com/kh/v=123&x=${x}&y=${y}&z=${z}",
            "http://khm3.google.com/kh/v=123&x=${x}&y=${y}&z=${z}"
        ],
    {
        //wrapDateLine: true,
        transitionEffect: "resize"
    }
),
    new OpenLayers.Layer.XYZ(
    "tianditu",
            [
    "http://tile0.chinaonmap.com/DataServer?T=vec_w&X=${x}&Y=${y}&L=${z}",

    "http://tile1.chinaonmap.com/DataServer?T=vec_w&X=${x}&Y=${y}&L=${z}",
    "http://tile2.chinaonmap.com/DataServer?T=vec_w&X=${x}&Y=${y}&L=${z}",
    "http://tile3.chinaonmap.com/DataServer?T=vec_w&X=${x}&Y=${y}&L=${z}",
    "http://tile4.chinaonmap.com/DataServer?T=vec_w&X=${x}&Y=${y}&L=${z}",

    "http://tile5.chinaonmap.com/DataServer?T=vec_w&X=${x}&Y=${y}&L=${z}",

    "http://tile6.chinaonmap.com/DataServer?T=vec_w&X=${x}&Y=${y}&L=${z}",

    "http://tile7.chinaonmap.com/DataServer?T=vec_w&X=${x}&Y=${y}&L=${z}"

],

    {

    }

),
    new OpenLayers.Layer.XYZ(
    "天地图中文注记",
            [
    "http://tile0.chinaonmap.com/DataServer?T=cva_w&X=${x}&Y=${y}&L=${z}",
    "http://tile1.chinaonmap.com/DataServer?T=cva_w&X=${x}&Y=${y}&L=${z}",
    "http://tile2.chinaonmap.com/DataServer?T=cva_w&X=${x}&Y=${y}&L=${z}",
    "http://tile3.chinaonmap.com/DataServer?T=cva_w&X=${x}&Y=${y}&L=${z}",
    "http://tile4.chinaonmap.com/DataServer?T=cva_w&X=${x}&Y=${y}&L=${z}",
    "http://tile5.chinaonmap.com/DataServer?T=cva_w&X=${x}&Y=${y}&L=${z}",
    "http://tile6.chinaonmap.com/DataServer?T=cva_w&X=${x}&Y=${y}&L=${z}",
    "http://tile7.chinaonmap.com/DataServer?T=cva_w&X=${x}&Y=${y}&L=${z}"
],

    {
        //wrapDateLine: true,

        isBaseLayer: false,

        visibility:false,

        displayInLayerSwitcher:false
    }

),
    new OpenLayers.Layer.XYZ(
    "天地图卫星图标注",
            [

    "http://tile0.chinaonmap.com/DataServer?T=cia_w&X=${x}&Y=${y}&L=${z}",

    "http://tile1.chinaonmap.com/DataServer?T=cia_w&X=${x}&Y=${y}&L=${z}",

    "http://tile2.chinaonmap.com/DataServer?T=cia_w&X=${x}&Y=${y}&L=${z}",

    "http://tile3.chinaonmap.com/DataServer?T=cia_w&X=${x}&Y=${y}&L=${z}",

    "http://tile4.chinaonmap.com/DataServer?T=cia_w&X=${x}&Y=${y}&L=${z}",
    "http://tile5.chinaonmap.com/DataServer?T=cia_w&X=${x}&Y=${y}&L=${z}",

    "http://tile6.chinaonmap.com/DataServer?T=cia_w&X=${x}&Y=${y}&L=${z}",
    "http://tile7.chinaonmap.com/DataServer?T=cia_w&X=${x}&Y=${y}&L=${z}"
],

    {
        // wrapDateLine: true,

        isBaseLayer: false
    }
)

],
    center: [0, 0],

    zoom: 1

});
ctrlLyrSwitcher=new OpenLayers.Control.LayerSwitcher();
map.addControl(ctrlLyrSwitcher);
map.addControl(new OpenLayers.Control.MousePosition());

map.events.register("changebaselayer", map, function (soso) {updateLayerVisibility(soso)});

}
OpenLayers.Util.onImageLoadError = function() {

    this.src = "http://www.openlayers.cn/olapi/img/blank.gif";

};
function updateLayerVisibility(soso)

{

    if(soso.layer.name=="tianditu"){

        map.layers[3].setVisibility(false);

        map.layers[3].displayInLayerSwitcher=false;

        map.layers[2].setVisibility(true);

        map.layers[2].displayInLayerSwitcher=true;

    }
else{

    map.layers[2].displayInLayerSwitcher=false;

    map.layers[2].setVisibility(false);

    map.layers[3].displayInLayerSwitcher=true;

    map.layers[3].setVisibility(true);

}

    ctrlLyrSwitcher.layerStates = [];

    ctrlLyrSwitcher.redraw();

}
</script>
</head>
<body onload="init()">
<h1 id="title">google卫星非偏移和天地图2.0</h1>
<div id="map" style="position:relative;width: 1000px;height: 600px;border: 1px solid #ccc;"></div>

</body>