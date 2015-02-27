/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var map, layer;
function init(){
             
             map = new OpenLayers.Map("map",{
                layers:[
                    new OpenLayers.Layer.GMapLayer("Google矢量图层",
                        ["http://192.168.2.103:8080/mapCache/maptile?mapower=google&maplayerid=6"])
                    
//                    new OpenLayers.Layer.GMapLayer("Google卫星图层",
//                        ["http://mt1.google.cn/vt/lyrs=s@123"]),
//                    new OpenLayers.Layer.GMapLayer("Google标注图层",
//                        ["http://mt2.google.cn/vt/imgtp=png32&lyrs=h@205000000"]),
//                    new OpenLayers.Layer.GMapLayer("Google矢量图层",
//                        ["http://mt1.google.cn/vt/lyrs=m@205000000"]),
//                    new OpenLayers.Layer.GMapLayer("Google地形图层",
//                        ["http://mt1.google.cn/vt/lyrs=t@130,r@205000000"]),
//                    new OpenLayers.Layer.GMapLayer("Google路况图层",
//                        ["http://mt0.google.com/vt?lyrs=m@205000000,traffic|seconds_into_week:-1"])
                ]
                ,center:new  OpenLayers.LonLat(120.12, 30.3)
            // Google.v3 uses web mercator as projection, so we have to
            // transform our coordinates
            .transform('EPSG:4326', 'EPSG:3857')
            });
            //图层切换控件
            map.addControl(new OpenLayers.Control.LayerSwitcher());
            
             map.addControl(new OpenLayers.Control.MousePosition({
        numDigits : 3,
        displayProjection:'EPSG:4326'
    }));
            //鹰眼控件
            map.addControl(new OpenLayers.Control.OverviewMap());
            map.zoomTo(10);
             
    /**google api
        map = new OpenLayers.Map('map', {
        projection: 'EPSG:3857',
        displayProjection:'EPSG:4326',
        layers: [
            new OpenLayers.Layer.Google(
                "Google Physical",
                {type: google.maps.MapTypeId.TERRAIN}
            ),  
            new OpenLayers.Layer.Google(
                "Google Streets", // the default
                {numZoomLevels: 20}
            ),
            new OpenLayers.Layer.Google(
                "Google Hybrid",
                {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20}
            ),
            new OpenLayers.Layer.Google(
                "Google Satellite",
                {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22}
            )
        ],
        center: new OpenLayers.LonLat(120, 30)
            // Google.v3 uses web mercator as projection, so we have to
            // transform our coordinates
            .transform('EPSG:4326', 'EPSG:3857'),
        zoom: 5
    });
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    
    
    map.addControl(new OpenLayers.Control.MousePosition({
        numDigits : 3,
        displayProjection:'EPSG:4326'
    }));
    
      map.events.register("mousemove", map, function(e) {
                var position = this.events.getMousePosition(e);
                testobj=this.events;
                OpenLayers.Util.getElement("coords").innerHTML = position;
            });
    **/
   
    /**
    function get_my_url(bounds){
        bounds.transform('EPSG:3857','EPSG:4326');          
        var x = parseInt(( ( (bounds.left  + 180) / 360 * (1 <<  this.map.getZoom()) ) + 1.5)); 
        var y =parseInt((Math.log(Math.tan(Math.PI/4-bounds.top/180*Math.PI/2))/Math.PI+1)/2*(1<< this.map.getZoom())+1.5);
        ; 
        var z = this.map.getZoom()+this.zoomOffset; 
        var path ="&X=" + x + "&Y=" + y + "&L=" + z; //L=${z}&Y=${y}&X=${x}
        var url = this.url; 
        if (url instanceof Array) { 
            url = this.selectUrl(path, url); 
        } 
        //alert(url+path); 
        return url + path; 
                 
                 
    }
    
    map = new OpenLayers.Map( 'map' ,{
        //resolutions:mapResolutions,
        //maxResolution: 611.496226171875*2*2*2*2*2*2*2, // corresponds to level 8 in the cache 
        //maxExtent:extentbak, 
        //restrictedExtent: extent, 
        // numZoomLevels: 3, 
        numZoomLevels:21,
        ///projection: new OpenLayers.Projection("EPSG:900913"),
        projection: 'EPSG:3857',
        displayProjection:'EPSG:4326',
        center: new OpenLayers.LonLat(119.99, 30.443)
        // Google.v3 uses web mercator as projection, so we have to
        // transform our coordinates
        .transform('EPSG:4326', 'EPSG:3857'),
               
        controls: [
        new OpenLayers.Control.Navigation({
            dragPanOptions : {
                enableKinetic : true
            }
        }),
        new OpenLayers.Control.PanZoomBar(),
        new OpenLayers.Control.MousePosition()
    ]
        });
                
             
    layer = new OpenLayers.Layer.XYZ( "ESRI",
        "http://192.168.2.103:8080/mapCache/maptile?mapower=google&maplayerid=6",{
            sphericalMercator: true,
            isBaseLayer:true,
            'getURL':get_my_url
        }
        //mapserverUrl+"?mapower=arcgisbundle&maplabel=tzsj&L=${z}&Y=${y}&X=${x}",
                        
        );
    map.addLayer(layer);
            
    var layerExtent = new OpenLayers.Bounds( 120, 31, 121 ,30);
    layerExtent.transform('EPSG:3857','EPSG:4326');
    //map.zoomToExtent(layerExtent);
    map.zoomTo(11);
   **/
//    
//    // add behavior to html
//    var animate = document.getElementById("animate");
//    animate.onclick = function() {
//        for (var i=map.layers.length-1; i>=0; --i) {
//            map.layers[i].animationEnabled = this.checked;
//        }
//    };
             
             
/**arcgis 缓存
             var agsTileOrigin = new OpenLayers.LonLat(-450359962737.0495,-450359962737.0495);
              var mapResolutions = [610.81167602539074,305.40583801269537,152.70291900634768,76.351459503173842
                ,38.175729751586921,19.087864875793461,9.5439324378967303,4.7719662189483651,2.3859831094741826,1.1929915547370913
            ,0.59649577736854564,0.29824788868427282,0.14912394434213641];
            var layerExtent = new OpenLayers.Bounds( -16130479.88466, 15972196.88466, -16126847.29495 ,15970073.35970);
            var layerOptions = {
                resolutions: mapResolutions,
                tileOrigin: agsTileOrigin,
                sphericalMercator: true
                //tileSize: TILE_SIZE
            };
            map = new OpenLayers.Map( 'map' ,{
                resolutions: mapResolutions,
                   tileSize: new OpenLayers.Size(256,256),
             controls: [
                        new OpenLayers.Control.Navigation({
        dragPanOptions : {
        enableKinetic : true
        }
    }),
                        new OpenLayers.Control.PanZoomBar(),
                        new OpenLayers.Control.MousePosition()]});
            layer = new OpenLayers.Layer.XYZ( "ESRI",
                    mapserverUrl+"?mapower=arcgisbundle&maplabel=tzsj&L=${z}&Y=${y}&X=${x}",
                    layerOptions    
                 );
            map.addLayer(layer);
            map.zoomToExtent(layerExtent);
            
            **/
}

