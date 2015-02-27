/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 */


        
function initmap(){
            
    map = new OpenLayers.Map( 'map',{
        
        maxExtent : new OpenLayers.Bounds(-180, -90, 180, 90),
        // projection : "EPSG:4490",
        projection: new OpenLayers.Projection('EPSG:4490'),//投影规则  
        displayProjection: new OpenLayers.Projection("EPSG:4326"),//显示的投影规则  
        resolutions : [  0.703125,0.3515625, 0.17578125, 0.087890625,
        0.0439453125, 0.02197265625, 0.010986328125,
        0.0054931640625, 0.00274658203125,
        0.001373291015625, 0.0006866455078125,
        0.00034332275390625, 0.000171661376953125,
        0.0000858306884765625, 0.00004291534423828125,
        0.000021457672119140625, 0.0000107288360595703125,
        0.00000536441802978515625, 0.000002682209014892578125,
        0.0000013411045074462890625, 0.0000006705522537231445,0.00000033527612686157227]
    } );
            
    //map.addControl( new OpenLayers.Control.LayerSwitcher() );
            
    map.addControl(new OpenLayers.Control.Navigation({
        dragPanOptions : {
            enableKinetic : true
        }
    }));
    map.addControl(new OpenLayers.Control.PanZoomBar());
                
    map.addControl(new OpenLayers.Control.MousePosition({
        numDigits : 3
    }));
    
    map.addControl(new OpenLayers.Control.OverviewMap());
            
            
            
}


    function lonlat2mercator(lon,lat){
         
         var mercatorx = lon *20037508.34/180;
         var mercatory= (Math.log(Math.tan((90+lat)*Math.PI/360))/(Math.PI/180))*20037508.34/180;
      
         var mecator=[mercatorx,mercatory];
         
         return mecator;
     
     }

function initgooglemap(){
    
    googlemap=new OpenLayers.Map("googlemap",{
                
        });
    //图层切换控件
    //googlemap.addControl(new OpenLayers.Control.LayerSwitcher());
    googlemap.addControl(new OpenLayers.Control.PanZoomBar());
            
    googlemap.addControl(new OpenLayers.Control.MousePosition({
        numDigits : 3,
        displayProjection:'EPSG:4326'
    }));
    
    googlemap.addControl(new OpenLayers.Control.Navigation({
        dragPanOptions : {
            enableKinetic : true
        }
    }));
    //鹰眼控件
    googlemap.addControl(new OpenLayers.Control.OverviewMap());
    
    
    
}

function initarcgismap(mapResolutions){
        
    arcgismap = new OpenLayers.Map( 'arcgismap' ,{
        resolutions: mapResolutions,
        //projection: new OpenLayers.Projection("EPSG:900913"),
        //displayProjection: new OpenLayers.Projection("EPSG:4326"),
        tileSize: new OpenLayers.Size(256,256),
        controls: [
        new OpenLayers.Control.Navigation({
            dragPanOptions : {
                enableKinetic : true
            }
        }),
        new OpenLayers.Control.PanZoomBar(),
        new OpenLayers.Control.MousePosition()]
    });
}


function initlayers(mapower,maplabel,layerid,layerindex,isdisplayed){
    var mirrorUrls=[];
    Ext.each(mapserverurls,function(a){
        mirrorUrls.push(a+'?maplayerid='+layerid+"&mapower="+mapower);
    
    })
    
    if(mapower=='tianditu'){
       
        
        if(map==null)initmap();
        //if(map.getLayersByName(layerid).length>0) return; 
        var tianditulayer =(function (){
              if(map.getLayersByName(layerid).length>0){
                  map.getLayersByName(layerid)[0].isBaseLayer=map.layers.length>0?false:true;
                  return map.getLayersByName(layerid)[0];
                  
              } 
              else {
                  
                  return  new OpenLayers.Layer.TiandituLayer(layerid,
            //mapserverUrl+'?mapower='+mapower+"&maplabel="+maplabel
            mirrorUrls[0]
            ,{
                mapType:maplabel,
                //id:"layerid",
                isBaseLayer:map.layers.length>0?false:true,
                topLevel: 1,
                bottomLevel: 20,
                maxExtent: (new OpenLayers.Bounds(-180, -90, 180, 90)).transform(new OpenLayers.Projection("EPSG:4326"),map.getProjectionObject()),
                mirrorUrls:mirrorUrls
            });
              }
            
        })();
        map.addLayer(tianditulayer);
        map.setLayerIndex(tianditulayer, 0);    
        if(tianditulayer == map.baseLayer){
            var baselayer = map.layers[0];
            map.setBaseLayer(baselayer);
    						
            tianditulayer.setIsBaseLayer(false);
            tianditulayer.setVisibility(true);
            map.resetLayersZIndex();
        }
       tianditulayer.setVisibility(isdisplayed);
        
      
        
         
        
    }
    else if(mapower=='google'){
        if(googlemap==null)initgooglemap();
       
        var glayer=(function (){
               if(googlemap.getLayersByName(layerid).length>0) return googlemap.getLayersByName(layerid)[0] ;
            else{
                return new OpenLayers.Layer.GMapLayer(layerid,
            mirrorUrls,{
                mapType:maplabel,
                //id:"layerid",
                isBaseLayer:googlemap.layers.length>0?false:true
                            
                            
            });
                
            }
        })();
        googlemap.addLayer(glayer);
        googlemap.setLayerIndex(glayer, layerindex);    
        
        if(glayer == googlemap.baseLayer){
            var baselayer =googlemap.layers[0];
            googlemap.setBaseLayer(baselayer);
    						
            glayer.setIsBaseLayer(false);
            glayer.setVisibility(true);
            googlemap.resetLayersZIndex();
        }
          glayer.setVisibility(isdisplayed);
        
    }
    else if(mapower=='arcgisbundle'){
        var layerOptions=null;
        var agsTileOrigin = null;//new OpenLayers.LonLat(-450359962737.0495,-450359962737.0495);
        var mapResolutions =[];/** [610.81167602539074,305.40583801269537,152.70291900634768,76.351459503173842
                ,38.175729751586921,19.087864875793461,9.5439324378967303,4.7719662189483651,2.3859831094741826,1.1929915547370913
            ,0.59649577736854564,0.29824788868427282,0.14912394434213641];**/
        var layerExtent = new OpenLayers.Bounds( -16046655, 16084806.88466, -15872574.29495 ,16175054.35970);
            
        Ext.Ajax.request({
            url:'cacheconfig',
            params:{
                maplabel:maplabel
            },
            success:function (response, option) {
                //alert(response.responseText);
                
                var rep_obj = Ext.JSON.decode(response.responseText);
                //testobj=rep_obj;
                agsTileOrigin=new  OpenLayers.LonLat(rep_obj.TileCacheInfo.SpatialReference.XOrigin,rep_obj.TileCacheInfo.SpatialReference.YOrigin);
                Ext.each(rep_obj.TileCacheInfo.LODInfos.LODInfo,function(a){
                    mapResolutions.push(parseFloat(a.Resolution));
                });
                layerOptions = {
                    resolutions: mapResolutions,
                    tileOrigin: agsTileOrigin,
                    getURL: function (bounds) {
                        var xyz = this.getXYZ(bounds);
                        var url = this.url;
                        if (OpenLayers.Util.isArray(url)) { 
                            var s = '' + xyz.x + xyz.y + xyz.z;
                            url = this.selectUrl(s, url) +"&L=${z}&Y=${y}&X=${x}";
                        }
                        return OpenLayers.String.format(url, xyz);
                    },
                    sphericalMercator: true
                //tileSize: TILE_SIZE
                };
                            
                            
                if(arcgismap==null)initarcgismap(mapResolutions);
                var layer =(function(){
                     if(arcgismap.getLayersByName(layerid).length>0) return arcgismap.getLayersByName(layerid)[0] ;
                     else{
                         return new OpenLayers.Layer.XYZ( layerid,
                    // mapserverUrl+'?mapower='+mapower+"&maplabel="+maplabel+"&maplayerid="+layerid+"&L=${z}&Y=${y}&X=${x}",
                    mirrorUrls,
                    layerOptions    
                    );
                         
                     }
                    
                })();
                
                arcgismap.addLayer(layer);
                arcgismap.zoomToExtent(layerExtent);
                //layer.setVisibility(true); 
               layer .setVisibility(isdisplayed);

            }
        });
        
            
            
            
            
        
        

    }
    
    
    var lon = 120.144;
    var lat = 30.246;
    var zoom = 10;
    var center= new OpenLayers.LonLat(lon, lat);
   
          
    if(map!=null)map.setCenter(center, zoom);
    if(googlemap!=null)googlemap.setCenter(center.transform('EPSG:4326', 'EPSG:3857'), zoom);
    
    
            

}