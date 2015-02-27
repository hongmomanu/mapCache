/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function initlayout() {

             //alert(2);
    // Simple ComboBox using the data store
    var layer_win = null;
    var layerdata_win = null;
    var maplayer_win = null;
    var box_control = null;
    var downHaveSession = {
        run: function () {
            downmissionstore.loadPage(1);
        },
        interval: 6000 //1000=1 second
    };
    var IsHaveSession = {
        run: function () {
            missionstore.loadPage(1);
        },
        interval: 6000 //1000=1 second
    };

    function clearLayers(maplayers, arcgislayers, googlelayers) {

        Ext.each(maplayers, function (layer) {
            alert(maplayers.length);
            map.removeLayer(layer);
        });
        Ext.each(arcgislayers, function (layer) {

            arcgismap.removeLayer(layer);
        });
        Ext.each(googlelayers, function (layer) {

            googlemap.removeLayer(layer);
        });
//       document.getElementById('map').innerHTML="";
//       document.getElementById('googlemap').innerHTML="";
//        document.getElementById('arcgismap').innerHTML="";
//        map=null;
//        googlemap=null;
//        arcgismap=null;
        /**
         if(map!=null){
            for(var i=map.layers.length-1;i>=0;i--){
                                    
                                map.removeLayer(map.layers[i]);
                            }
        
            map=null;
        }

         if(googlemap!=null){
            
             for(var i=googlemap.layers.length-1;i>=0;i--){
                                    
                    googlemap.removeLayer(googlemap.layers[i]);
                }
            googlemap=null;
        }

         if(arcgismap!=null){
            
             for(var i=arcgismap.layers.length-1;i>=0;i--){
                                    
                                arcgismap.removeLayer(arcgismap.layers[i]);
                }
            arcgismap=null;
        }

         **/

    }

    Ext.define('layerLevel', {
        extend: 'Ext.data.Model',
        fields: [
            {
                type: 'string',
                name: 'name'
            },

            {
                type: 'string',
                name: 'value'
            }
        ]
    });

    /*
     *生成级数组
     **/
    var levels = [];
    var levelstore = Ext.create('Ext.data.Store', {
        autoDestroy: true,
        model: 'layerLevel',
        data: levels
    });


    // The data store holding the states
    var mapserverstore = Ext.create('Ext.data.Store', {
        //autoLoad: true, 
        proxy: {
            actionMethods: 'post',
            type: 'ajax',
            extraParams: {
                ismap: true,
                maptype: 0
            },
            url: "maptree"
        },
        fields: ['name', 'text'],
        listeners: {
        }
    })
    mapserverstore.load();

    var maplayerstore = Ext.create('Ext.data.Store', {
        // autoLoad: true, 
        proxy: {
            actionMethods: 'post',
            type: 'ajax',

            url: "maptree"
        },
        fields: ['layer', 'text'],
        listeners: {

        }
    });

    var layerdatastore = Ext.create('Ext.data.Store', {
        // autoLoad: true, 
        proxy: {
            actionMethods: 'post',
            type: 'ajax',

            url: "maptree"
        },
        fields: ['text', 'layerdataid', 'minlevel', 'maxlevel', 'layerlabel'],
        listeners: {

        }
    })

    var levelcombo = Ext.create('Ext.form.ComboBox', {
        fieldLabel: '图层级别',
        name: 'layerlevel',
        xtype: 'combo',
        multiSelect: true,
        displayField: 'name',
        emptyText: '请选择图层级别',
        allowBlank: false,
        blankText: "不能为空，请选择",
        disabled: true,
        valueField: 'value',
        queryMode: 'local',
        flex: 1,
        store: levelstore

    });


    var layerdatacombo = Ext.create('Ext.form.ComboBox', {
        fieldLabel: '图层数据源',
        name: 'layerid',
        xtype: 'combo',
        displayField: 'text',
        emptyText: '请选择图层数据源',
        //allowBlank:false,
        blankText: "不能为空，请选择",
        disabled: true,
        valueField: 'layerdataid',
        queryMode: 'local',
        flex: 1,
        listeners: {
            scope: this,
            'select': function (combo, records) {
                levels = [];
                for (var i = records[0].data.minlevel; i <= records[0].data.maxlevel; i++) {
                    levels.push({
                        name: i + "级",
                        value: i
                    });
                }
                levelstore.removeAll();
                levelstore.add(levels);
                levelcombo.enable();
                //Ext.getCmp('hidelayerlabeltext').setValue(records[0].data.layerlabel);
            }
        },
        store: layerdatastore

    });


    Ext.define('missionThread', {
        extend: 'Ext.data.Model',
        fields: [
            'taskid', 'mapname', 'layername', 'mapower', 'mapid',
            {
                name: 'layerlevel',
                type: 'int'
            }, {
                name: 'state',
                type: 'int'
            },
            {
                name: 'status',
                type: 'int'
            },
            {
                name: 'minx',
                type: 'double'
            }, {
                name: 'maxx',
                type: 'double'
            },
            {
                name: 'miny',
                type: 'double'
            }, {
                name: 'maxy',
                type: 'double'
            },
            {
                name: 'bgtm',
                mapping: 'bgtm',
                type: 'long'
                //dateFormat: 'timestamp'
            },
            {
                name: 'updatetime',
                mapping: 'updatetime',
                type: 'long'

            },
            {
                name: 'failnum',
                mapping: 'failnum',
                type: 'int'
                //dateFormat: 'timestamp'
            },
            {
                name: 'totalnum',
                mapping: 'totalnum',
                type: 'int'
                //dateFormat: 'timestamp'
            },
            {
                name: 'edtm',
                mapping: 'edtm',
                type: 'long'
                //dateFormat: 'timestamp'
            }
        ],
        idProperty: 'taskid'
    });

    var downmissionstore = Ext.create('Ext.data.Store', {
        pageSize: 10,
        model: 'missionThread',
        remoteSort: false,

        listeners: {
            load: function (records) {
                //Ext.getCmp('cancelbtn').disable();
                //Ext.getCmp('refinishbtn').disable();
                Ext.getCmp('downdeltaskbtn').disable();

                var stop_flag = true;
                Ext.each(records.data.items, function (rec) {
                    if (rec.data.status == 1 || rec.data.status == -1) {
                        stop_flag = false;
                    }
                });
                if (stop_flag) {
                    Ext.TaskManager.stop(downHaveSession);
                }

            }


        },
        proxy: {
            type: 'ajax',
            url: 'downtask',
            extraParams: {
                // state:2
            },

            reader: {
                type: 'json',
                root: 'results',
                totalProperty: 'totalCount'
            }
        },
        sorters: [
            {
                property: 'lastpost',
                direction: 'DESC'
            }
        ]
    });


    var missionstore = Ext.create('Ext.data.Store', {
        pageSize: 10,
        model: 'missionThread',
        remoteSort: false,

        listeners: {
            load: function (records) {
                Ext.getCmp('cancelbtn').disable();
                Ext.getCmp('refinishbtn').disable();
                Ext.getCmp('deltaskbtn').disable();

                var stop_flag = true;
                Ext.each(records.data.items, function (rec) {
                    if (rec.data.state == 1 || rec.data.state == -1) {
                        stop_flag = false;
                    }
                });
                if (stop_flag) {
                    Ext.TaskManager.stop(IsHaveSession);
                }
                /**
                 if(records.totalCount>0){
                        var first_record=records.data.items[0];
                        if(first_record.data.state!=1&&first_record.data.state!=-1){
                            Ext.TaskManager.stop(IsHaveSession);
                        }
                  }  **/
            }


            /**
             msgTip = Ext.MessageBox.show({
                   title:'提示',
                  width : 250,
                   msg:'页面报表统计信息刷新中,请稍后......'
                });**/
        },
        proxy: {
            type: 'ajax',
            url: 'maptask',
            extraParams: {
                state: 2
            },

            reader: {
                type: 'json',
                root: 'results',
                totalProperty: 'totalCount'
            }
        },
        sorters: [
            {
                property: 'lastpost',
                direction: 'DESC'
            }
        ]
    });


    Ext.define('servertree', {
        extend: 'Ext.data.Model',
        fields: [
            {
                name: 'text',
                type: 'string'
            },

            {
                name: 'key',
                type: 'int'
            },

            {
                name: 'treelevel',
                type: 'int'
            },

            {
                name: 'minlevel',
                type: 'int'
            },

            {
                name: 'maxlevel',
                type: 'int'
            },

            {
                name: 'updatetime',
                type: 'long'
            },

            {
                name: 'mapower',
                type: 'string'
            },

            {
                name: 'name',
                type: 'string'
            },

            {
                name: 'maplabel',
                type: 'string'
            },

            {
                name: 'projection',
                type: 'string'
            },

            {
                name: 'spatialreference',
                type: 'string'
            },

            {
                name: 'maptype',
                type: 'string'
            },


            {
                name: 'ispublic',
                type: 'string'
            },


            {
                name: 'layerlabel',
                type: 'string'
            }
        ]
    });

    var mapservertreestore = Ext.create('Ext.data.TreeStore', {
        model: 'servertree',
        proxy: {
            type: 'ajax',
            //the store will get the content from the .json file
            url: 'maptree?isall=true'
        },
        folderSort: true
    });


    var mapserverPanel = Ext.create('Ext.tree.Panel', {
        // hidden:true,

        title: '服务资源管理',

        layout: 'fit',
        // collapsible: true,
        anchor: '100% 60%',
        useArrows: true,
        rootVisible: false,
        store: mapservertreestore,
        multiSelect: false,
        items: [


        ],
        //singleExpand: true,
        //        tbar:[
        //           '->',Ext.create('Ext.Button', {
        //    text: '新增资源',
        //    id:'newresbtn',
        //    disabled:true,
        //    renderTo: Ext.getBody(),
        //    handler: function() {
        //        alert('新增资源!');
        //    }
        //})
        //        ],

        //the 'columns' property is now 'headers'
        columns: [
            {
                xtype: 'treecolumn', //this is so we know which column will show the tree
                text: '资源名',
                flex: 2,
                sortable: true,
                dataIndex: 'text'
            },
            {
                text: '更新时间',
                flex: 1,
                dataIndex: 'updatetime',
                renderer: function (val) {
                    var time = new Date(val);
                    val = Ext.util.Format.date(time, 'Y-m-d H:i');
                    return val;
                },
                sortable: true
            }
        /**, {
            text: '删除',
            width: 40,
            menuDisabled: true,
            xtype: 'actioncolumn',
            tooltip: '删除所选资源',
            align: 'center',
            icon: 'img/edit_task.png',
            handler: function(grid, rowIndex, colIndex, actionItem, event, record, row) {
                
                Ext.Msg.alert('Editing' + (record.get('layerlabel') ? ' completed task' : '') , record.get('key')+":"+record.get('treelevel'));
            }
        }**/
        ]
    });


    var mapserverForm = Ext.create('Ext.form.Panel', {
        id: 'mapserverPanel',
        frame: true,
        header: false,
        hidden: true,
        title: '服务管理',
        //bodyPadding: 5,
        //width: 750,
        layout: 'anchor',    // Specifies that the items will now be arranged in columns

        fieldDefaults: {
            labelAlign: 'left',
            msgTarget: 'side'
        },

        items: [mapserverPanel],
        buttons: [
            {
                text: '新增地图',
                handler: function () {
                    //alert("ok");

                    if (maplayer_win != null)maplayer_win.show();
                    else {
                        var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';
                        var form = Ext.widget('form', {
                            layout: {
                                type: 'vbox',

                                align: 'stretch'
                            },
                            border: false,
                            bodyPadding: 10,
                            //xtype: 'fieldset',

                            fieldDefaults: {
                                labelAlign: 'top',
                                labelWidth: 100,
                                labelStyle: 'font-weight:bold'
                            },

                            items: [
                                {
                                    xtype: 'textfield',
                                    fieldLabel: '地图服务名',
                                    afterLabelTextTpl: required,
                                    name: 'text'
                                },
                                {
                                    xtype: 'textfield',
                                    fieldLabel: '服务标识',
                                    afterLabelTextTpl: required,
                                    name: 'name'
                                },
                                {
                                    xtype: 'textfield',
                                    fieldLabel: '投影坐标',
                                    afterLabelTextTpl: required,
                                    name: 'projection'
                                },
                                {
                                    //xtype: 'datefield',
                                    xtype: 'textfield',
                                    afterLabelTextTpl: required,
                                    fieldLabel: '空间坐标系',
                                    name: 'spatialreference'
                                },
                                {
                                    xtype: 'radiogroup',
                                    fieldLabel: '服务类型',
                                    columns: 2,

                                    //labelWidth:200,
                                    // labelAlign : 'right',
                                    //width:360,
                                    defaults: {
                                        name: 'maptype' //Each radio has the same name so the browser will make sure only one is checked at once
                                    },
                                    items: [
                                        {
                                            inputValue: '0',
                                            width: 80,
                                            checked: true,
                                            boxLabel: '网络地图'
                                        },
                                        {
                                            inputValue: '1',
                                            width: 180,
                                            boxLabel: 'arcgis紧凑数据'
                                        }
                                    ]
                                }/**,
                             {
                //xtype: 'datefield',
                fieldLabel: '服务类型',
                name: 'maptype'
            }**/
                            ],

                            buttons: [
                                {
                                    text: '取消',
                                    handler: function () {
                                        //this.up('form').getForm().reset();
                                        this.up('window').close();
                                        maplayer_win = null;
                                    }
                                },
                                {
                                    text: '添加',
                                    handler: function () {
                                        var form = this.up('form').getForm();
                                        if (form.isValid()) {
                                            //Ext.MessageBox.alert('Submitted Values', form.getValues(true));

                                            form.submit({
                                                waitTitle: '提示', //标题
                                                waitMsg: '正在保存数据请稍后...', //提示信息                    
                                                url: 'mapinfotodb',

                                                method: "POST",
                                                params: {
                                                    type: 'add',
                                                    keyid: 0,
                                                    treelevel: -1
                                                },
                                                success: function (form, action) {

                                                    Ext.Msg.alert('成功!', "添加数据成功!");
                                                    mapserverPanel.getStore().load();
                                                    mapserverForm.remove(mapserverForm.items.items[1]);
                                                    maplayer_win.close();
                                                    maplayer_win = null;

                                                },
                                                failure: function (form, action) {
                                                    Ext.Msg.alert('失败!', "添加数据失败!");

                                                }
                                            });


                                            // In a real application, this would submit the form to the configured url
                                            // this.up('form').getForm().submit();
                                            //form.reset();
                                            //this.up('window').hide();
                                            //Ext.MessageBox.alert('Thank you!', 'Your inquiry has been sent. We will respond as soon as possible.');
                                        }
                                    }
                                }
                            ]
                        });


                        maplayer_win = Ext.widget('window', {
                            title: '添加地图',
                            closeAction: 'hide',
                            width: 400,
                            height: 400,
                            layout: 'fit',
                            resizable: true,
                            modal: true,
                            items: form
                        });
                        maplayer_win.show();
                    }


                }

            },
            '->',
            {
                text: '删除',
                id: 'delServerInfobtn',
                disabled: true,
                handler: function () {

                    var select_arr = mapserverPanel.items.items[0].getSelectionModel().getSelection();
                    Ext.MessageBox.confirm('提示确认', '确定要删除所选资源么?', function (btn) {
                        if (btn === 'yes') {
                            Ext.Ajax.request({
                                url: 'mapinfotodb',
                                params: {
                                    type: 'del',
                                    keyid: select_arr[0].data.key,
                                    treelevel: select_arr[0].data.treelevel
                                },
                                success: function (response, option) {
                                    var rep_obj = Ext.JSON.decode(response.responseText);
                                    if (rep_obj.success) {
                                        Ext.MessageBox.alert('成功!', '查看状态列表！', function (btn) {
                                            mapserverPanel.getStore().load();
                                            mapserverForm.remove(mapserverForm.items.items[1]);

                                        });
                                    }


                                }
                            });


                        }
                    });


                    //this.up('form').getForm().reset();
                }
            },
            {
                text: '保存',
                id: 'saveServerInfobtn',
                disabled: true,
                handler: function () {

                    var form = mapserverForm.items.items[0].up('form').getForm();
                    var select_arr = mapserverPanel.items.items[0].getSelectionModel().getSelection();
                    if (form.isValid()) {

                        form.submit({
                            waitTitle: '提示', //标题
                            waitMsg: '正在保存数据请稍后...', //提示信息                    
                            url: 'mapinfotodb',
                            method: "POST",
                            params: {
                                type: 'update',
                                keyid: select_arr[0].data.key,
                                treelevel: select_arr[0].data.treelevel
                            },
                            success: function (form, action) {
                                Ext.Msg.alert('成功!', "保存数据成功!");
                                // var westPanel=Ext.getCmp('west-panel');
                                //westPanel.items.items[0].expand();
                                mapserverPanel.getStore().load();
                                mapserverForm.remove(mapserverForm.items.items[1]);
                                /**
                                 console.log(select_arr[0].data);
                                 if(select_arr[0].data.treelevel==1){
                                    var treepanel=Ext.getCmp('maptree');     
                                    Ext.each(treepanel.getRootNode().childNodes,function(child){
                                      if(child.raw['name']==select_arr[0].data.mapower){
                                          
                                          if(child.childNodes.length>0){                                              
                                                            child.insertBefore({
                                                            text: select_arr[0].data.text,
                                                            leaf: true,
                                                            checked:true,
                                                            layer: select_arr[0].data.key,
                                                            mapower:select_arr[0].data.mapower,
                                                            maplabel:select_arr[0].data.maplabel
                                                        }, child.childNodes[0]);
                                                   var layerindex=child.childNodes.length;
                                              initlayers(select_arr[0].data.mapower,select_arr[0].data.maplabel,select_arr[0].data.key,layerindex,true);   
                                          }
                                          //child.remove(true);
                                          //child.set('text',child.raw['text']+"(--当前浏览地图)");
                                              //Ext.get('map').setDisplayed(true);
                                              //Ext.get('arcgismap').setDisplayed(false);
                                              //Ext.get('googlemap').setDisplayed(false);

                                      }

                                  });
        
                        
                        
                                
                            }


                                 **/

                                //clearLayers();

                                /***
                                 for(var i=map.layers.length-1;i>=0;i--){
                                    
                                map.removeLayer(map.layers[i]);
                            }

                                 for(var j=googlemap.layers.length-1;j>=0;j--){
                                    
                                googlemap.removeLayer(googlemap.layers[j]);
                            }
                                 **/



                                maptree.getStore().load();
                                // clearLayers(maplayers,arcgislayers,googlelayers);

                            },
                            failure: function (form, action) {

                                if (action.failureType === Ext.form.action.Action.CONNECT_FAILURE) {
                                    Ext.Msg.alert('Error',
                                        'Status:' + action.response.status + ': ' +
                                            action.response.statusText);
                                }

                            }
                        });

                    }


                    //this.up('form').getForm().reset();
                }
            },
            {
                text: '新增资源',
                id: 'newresbtn',
                disabled: true,
                handler: function () {
                    var select_arr = mapserverPanel.items.items[0].getSelectionModel().getSelection();
                    if (select_arr.length == 0) {
                        Ext.MessageBox.alert('提醒!', '请确认选择父资源！', function (btn) {
                        });
                    }
                    else {
                        if (select_arr[0].data.treelevel == 0) {
                            if (layer_win != null)layer_win.show();
                            else {
                                var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';
                                var form = Ext.widget('form', {
                                    layout: {
                                        type: 'vbox',
                                        align: 'stretch'
                                    },
                                    border: false,
                                    bodyPadding: 10,

                                    fieldDefaults: {
                                        labelAlign: 'top',
                                        labelWidth: 100,
                                        labelStyle: 'font-weight:bold'
                                    },
                                    items: [
                                        {
                                            xtype: 'textfield',
                                            fieldLabel: '图层名称',
                                            name: 'mapname',
                                            afterLabelTextTpl: required,
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'textfield',
                                            fieldLabel: '图层标识',
                                            name: 'maplabel',
                                            afterLabelTextTpl: required,
                                            allowBlank: false
                                        }
                                    ],

                                    buttons: [
                                        {
                                            text: '取消',
                                            handler: function () {
                                                this.up('form').getForm().reset();
                                                this.up('window').close();
                                                layer_win = null;
                                            }
                                        },
                                        {
                                            text: '添加',
                                            handler: function () {
                                                var form = this.up('form').getForm();


                                                if (form.isValid()) {
                                                    Ext.MessageBox.alert('Submitted Values', form.getValues(true));

                                                    form.submit({
                                                        waitTitle: '提示', //标题
                                                        waitMsg: '正在保存数据请稍后...', //提示信息                    
                                                        url: 'mapinfotodb',

                                                        method: "POST",
                                                        params: {
                                                            type: 'add',
                                                            keyid: select_arr[0].data.key,
                                                            treelevel: select_arr[0].data.treelevel
                                                        },
                                                        success: function (form, action) {

                                                            Ext.Msg.alert('成功!', "添加数据成功!");
                                                            mapserverPanel.getStore().load();
                                                            mapserverForm.remove(mapserverForm.items.items[1]);
                                                            layer_win.close();
                                                            layer_win = null;
                                                            //console.log(action);
                                                        },
                                                        failure: function (form, action) {

                                                            if (action.failureType === Ext.form.action.Action.CONNECT_FAILURE) {
                                                                Ext.Msg.alert('Error',
                                                                    'Status:' + action.response.status + ': ' +
                                                                        action.response.statusText);
                                                            }

                                                        }
                                                    });


                                                    // In a real application, this would submit the form to the configured url
                                                    // this.up('form').getForm().submit();
                                                    //form.reset();
                                                    //this.up('window').hide();
                                                    //Ext.MessageBox.alert('Thank you!', 'Your inquiry has been sent. We will respond as soon as possible.');
                                                }
                                            }
                                        }
                                    ]
                                });


                                layer_win = Ext.widget('window', {
                                    title: '添加图层',
                                    closeAction: 'hide',
                                    width: 400,
                                    height: 400,
                                    layout: 'fit',
                                    resizable: true,
                                    modal: true,
                                    items: form
                                });
                                layer_win.show();
                            }


                        } else if (select_arr[0].data.treelevel == 1) {

                            if (layerdata_win != null)layerdata_win.show();
                            else {
                                var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';
                                var form = Ext.widget('form', {
                                    layout: {
                                        type: 'vbox',
                                        align: 'stretch'
                                    },
                                    border: false,
                                    bodyPadding: 10,

                                    fieldDefaults: {
                                        labelAlign: 'top',
                                        labelWidth: 100,
                                        labelStyle: 'font-weight:bold'
                                    },
                                    items: [
                                        {
                                            xtype: 'textfield',
                                            fieldLabel: '图层数据名称',
                                            name: 'layername',
                                            afterLabelTextTpl: required,
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'textareafield',
                                            fieldLabel: '图层数据标识',
                                            name: 'layerlabel',
                                            afterLabelTextTpl: required,
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'textfield',
                                            fieldLabel: '图层数据最小级别',
                                            name: 'minlevel',
                                            afterLabelTextTpl: required,
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'textfield',
                                            fieldLabel: '图层数据最大级别',
                                            name: 'maxlevel',
                                            afterLabelTextTpl: required,
                                            allowBlank: false
                                        }
                                    ],

                                    buttons: [
                                        {
                                            text: '取消',
                                            handler: function () {
                                                //this.up('form').getForm().reset();
                                                this.up('window').close();
                                                layerdata_win = null;
                                            }
                                        },
                                        {
                                            text: '添加',
                                            handler: function () {
                                                var form = this.up('form').getForm();
                                                if (form.isValid()) {
                                                    //Ext.MessageBox.alert('Submitted Values', form.getValues(true));

                                                    form.submit({
                                                        waitTitle: '提示', //标题
                                                        waitMsg: '正在保存数据请稍后...', //提示信息                    
                                                        url: 'mapinfotodb',

                                                        method: "POST",
                                                        params: {
                                                            type: 'add',
                                                            keyid: select_arr[0].data.key,
                                                            treelevel: select_arr[0].data.treelevel
                                                        },
                                                        success: function (form, action) {

                                                            Ext.Msg.alert('成功!', "添加数据成功!");
                                                            mapserverPanel.getStore().load();
                                                            mapserverForm.remove(mapserverForm.items.items[1]);
                                                            layerdata_win.close();
                                                            layerdata_win = null;

                                                        },
                                                        failure: function (form, action) {
                                                            Ext.Msg.alert('失败!', "添加数据失败!");

                                                        }
                                                    });


                                                    // In a real application, this would submit the form to the configured url
                                                    // this.up('form').getForm().submit();
                                                    //form.reset();
                                                    //this.up('window').hide();
                                                    //Ext.MessageBox.alert('Thank you!', 'Your inquiry has been sent. We will respond as soon as possible.');
                                                }
                                            }
                                        }
                                    ]
                                });


                                layerdata_win = Ext.widget('window', {
                                    title: '添加图层',
                                    closeAction: 'hide',
                                    width: 400,
                                    height: 400,
                                    layout: 'fit',
                                    resizable: true,
                                    modal: true,
                                    items: form
                                });
                                layerdata_win.show();
                            }


                        }

                    }

                    //this.up('form').getForm().reset();newresbtn
                }
            }
        ]
    });


    function getrootform() {
        var form_item = {
            // margin: '0 0 0 10',
            xtype: 'fieldset',
            anchor: '100% 40%',
            //layout:'fit',
            title: '地图服务信息',
            defaults: {
                width: 240,
                labelWidth: 90
            },
            defaultType: 'textfield',
            items: [
                {
                    fieldLabel: '地图服务名',
                    name: 'text'
                },
                {
                    fieldLabel: '服务标识',
                    name: 'name'
                },
                {
                    fieldLabel: '投影坐标',
                    name: 'projection'
                },
                {
                    //xtype: 'datefield',
                    fieldLabel: '空间坐标系',
                    name: 'spatialreference'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '服务类型',
                    columns: 2,

                    //labelWidth:200,
                    // labelAlign : 'right',
                    //width:360,
                    defaults: {
                        name: 'maptype' //Each radio has the same name so the browser will make sure only one is checked at once
                    },
                    items: [
                        {
                            inputValue: '0',
                            width: 80,
                            checked: true,
                            boxLabel: '网络地图'
                        },
                        {
                            inputValue: '1',
                            width: 180,
                            boxLabel: 'arcgis紧凑数据'
                        }
                    ]
                }/**,
             {
                //xtype: 'datefield',
                fieldLabel: '服务类型',
                name: 'maptype'
            }**/
            ]


        };
        return   form_item;
    }

    function getlayerform() {

        var form_item = {
            // margin: '0 0 0 10',
            xtype: 'fieldset',
            anchor: '100% 40%',
            //layout:'fit',
            title: '地图图层信息',
            defaults: {
                width: 240,
                labelWidth: 90
            },
            defaultType: 'textfield',
            items: [
                {
                    fieldLabel: '地图图层名',
                    name: 'text'
                },
                {
                    fieldLabel: '地图图层标识',
                    name: 'maplabel'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否发布',
                    columns: 2,
                    defaults: {
                        name: 'ispublic' //Each radio has the same name so the browser will make sure only one is checked at once
                    },
                    items: [
                        {
                            inputValue: 'false',

                            boxLabel: '未发布'
                        },
                        {
                            inputValue: 'true',
                            boxLabel: '已发布'
                        }
                    ]
                }

            ]


        };
        return   form_item;

    }

    function getdataform() {
        var form_item = {
            // margin: '0 0 0 10',
            xtype: 'fieldset',
            anchor: '100% 40%',
            //layout:'fit',
            title: '图层数据信息',
            defaults: {
                width: 600,
                labelWidth: 90
            },
            layout: {
                // type: 'fit',
                // align: 'stretch'
            },
            defaultType: 'textfield',
            items: [
                {
                    fieldLabel: '图层数据名',
                    name: 'text'
                },
                {
                    xtype: 'textareafield',
                    fieldLabel: '图层数据地址',
                    name: 'layerlabel'
                },
                {
                    fieldLabel: '数据最小级别',
                    name: 'minlevel'
                },
                {
                    fieldLabel: '数据最大级别',
                    name: 'maxlevel'
                }
            ]


        };
        return   form_item;

    }

    mapserverPanel.getSelectionModel().on('selectionchange', function (sm, selectedRecord) {
        if (selectedRecord.length) {
            mapserverForm.remove(mapserverForm.items.items[1]);
            Ext.getCmp('saveServerInfobtn').enable();
            Ext.getCmp('delServerInfobtn').enable();
            Ext.getCmp('newresbtn').enable();

            var treelevel = selectedRecord[0].data['treelevel'];

            if (treelevel == 0) {
                Ext.getCmp('delServerInfobtn').enable();
                mapserverForm.add(getrootform());
            } else if (treelevel == 1) {
                mapserverForm.add(getlayerform());
                if(selectedRecord[0].parentNode.data['maptype']==="1"){
                    Ext.getCmp('newresbtn').disable();

                };
            } else if (treelevel == 2) {
                mapserverForm.add(getdataform());
                Ext.getCmp('newresbtn').disable();
            }
            mapserverForm.items.items[0].up('form').getForm().loadRecord(selectedRecord[0]);
        }
    });

    var missionformPanel = Ext.create('Ext.grid.Panel', {
        //width: 700,
        //height: 500,
        title: '任务状态列表',
        store: missionstore,
        id: 'missionformPanel',
        //disableSelection: true,

        viewConfig: {
            id: 'gv',
            trackOver: false,
            loadMask: false,
            stripeRows: false
        },
        // grid columns
        columns: [
            {
                id: 'taskid',
                text: "地图名称",
                dataIndex: 'mapname',
                //flex: 1,
                //renderer: renderTopic,
                sortable: false
            },
            {
                text: "数据图层",
                dataIndex: 'layername',
                width: 160,
                hidden: false,
                sortable: true
            },
            {
                text: "图层级别",
                dataIndex: 'layerlevel',
                width: 70,
                align: 'center'
            },
            {
                text: "最小纬度",
                dataIndex: 'miny',
                xtype: "numbercolumn",
                format: '0.000',
                flex: 1,
                align: 'center'
            },
            {
                text: "最大纬度",
                dataIndex: 'maxy',
                xtype: "numbercolumn",
                format: '0.000',
                width: 70,
                flex: 1,
                align: 'center'
            },
            {
                text: "最小经度",
                dataIndex: 'minx',
                flex: 1,
                xtype: "numbercolumn",
                format: '0.000',
                align: 'center'
            },
            {
                text: "最大经度",
                xtype: "numbercolumn",
                format: '0.000',
                dataIndex: 'maxx',
                flex: 1,
                align: 'center'
            },
            {
                //id: 'last',
                text: "任务状态",
                dataIndex: 'state',
                width: 120,
                renderer: function (val, metaData, record) {
                    if (val == 2) {
                        return '<font color="green">已完成</font>';
                    } else if (val == 1) {
                        return '<font color="blue">正在进行:' + (record.data.totalnum - record.data.failnum) + "/" + record.data.totalnum + '</font>';
                    }
                    else if (val == 0) {
                        return '<font color="red">失败瓦片数:' + record.data.failnum + '</font>';
                    }
                    else if (val == -1) {

                        return '<font color="black">正在初始化</font>';
                    }
                }

            },
            {

                text: "开始时间",
                dataIndex: 'bgtm',
                width: 150,
                renderer: function (val) {
                    var time = new Date(val);
                    //console.log(time);
                    val = Ext.util.Format.date(time, 'Y-m-d H:i');
                    return val;
                }




            },
            {
                text: "结束时间",
                dataIndex: 'edtm',
                width: 150,
                renderer: function (val, obj, record) {
                    if (record.data.state == 1) {
                        return "";
                    }
                    var time = new Date(val);
                    val = Ext.util.Format.date(time, 'Y-m-d H:i');
                    return val;
                }

            },
            {
                text: '按钮',
                flex: 1,
                align: 'center',
                hidden: true,
                renderer: function renderChoose(value, p, r) {
                }


            }

        ],
        tbar: [
            {
                text: "已完成",
                pressed: true,
                enableToggle: true,
                toggleGroup: 1,
                handler: function () {
                    missionstore.proxy.extraParams.username = Ext.getCmp('username_key').getValue();
                    missionstore.proxy.extraParams.bgtm = Ext.getCmp('bgtmdate').getValue();
                    missionstore.proxy.extraParams.mapower = Ext.getCmp('mapownersearchcomb').getValue();
                    var edtm = Ext.getCmp('edtmdate').getValue();
                    edtm.setDate(edtm.getDate() + 1);
                    missionstore.proxy.extraParams.edtm = edtm;
                    missionstore.proxy.extraParams.state = 2;
                    missionstore.loadPage(1);
                }
            },
            {
                text: "未完成",
                enableToggle: true,
                toggleGroup: 1,
                handler: function () {
                    missionstore.proxy.extraParams.username = Ext.getCmp('username_key').getValue();
                    missionstore.proxy.extraParams.mapower = Ext.getCmp('mapownersearchcomb').getValue();
                    missionstore.proxy.extraParams.bgtm = Ext.getCmp('bgtmdate').getValue();
                    var edtm = Ext.getCmp('edtmdate').getValue();
                    edtm.setDate(edtm.getDate() + 1);
                    missionstore.proxy.extraParams.edtm = edtm;
                    missionstore.proxy.extraParams.state = '0,1,-1';
                    missionstore.loadPage(1);
                    Ext.TaskManager.start(IsHaveSession);
                }
            },
            {
                xtype: 'button',
                text: '<span style="font-family:宋体;font-weight:bold">>></span>',
                tooltip: '显示/隐藏内容',
                //id: 'westpanelhandler',
                handler: function () {
                    if (this.nextSibling().isHidden()) {
                        this.nextSibling().show();
                        this.nextSibling().nextSibling().show();
                        this.nextSibling().nextSibling().nextSibling().show();
                        this.nextSibling().nextSibling().nextSibling().nextSibling().show();
                        this.setText('<span style="font-family:宋体;font-weight:bold"><<</span>');
                    } else {
                        this.nextSibling().hide();
                        this.nextSibling().nextSibling().hide();
                        this.nextSibling().nextSibling().nextSibling().hide();
                        this.nextSibling().nextSibling().nextSibling().nextSibling().hide();
                        this.setText('<span style="font-family:宋体;font-weight:bold">>></span>');
                    }

                }
            },
            {
                fieldLabel: '地图选择',
                name: 'mapowner',
                hidden: true,
                id: 'mapownersearchcomb',
                labelWidth: 80,
                labelAlign: 'right',
                //width:80,   
                minWidth: 180,
                xtype: 'combo',
                allowBlank: false,
                blankText: "不能为空，请选择",
                displayField: 'text',
                valueField: 'name',
                emptyText: '请选择地图',
                listeners: {
                    scope: this,
                    'select': function (combo, records) {
                        /**
                         combo.next().enable();
                         maplayerstore.getProxy().extraParams={
                                mapower:combo.getValue()
                            };
                         maplayerstore.load();
                         }**/
                    }
                },
                queryMode: 'local',
                flex: 1,
                store: mapserverstore

            },
            {
                xtype: 'datefield',
                labelWidth: 80,
                labelAlign: 'right',
                fieldLabel: '起始日期',
                name: 'bgtm',
                id: 'bgtmdate',
                hidden: true,
                // The value matches the format; will be parsed and displayed using that format.
                format: 'Y-m-d',
                value: '2012-12-21'
            },
            {
                xtype: 'datefield',
                //anchor: '100%',
                hidden: true,
                id: 'edtmdate',
                labelWidth: 80,
                labelAlign: 'right',
                fieldLabel: '结束日期',
                name: 'edtm',
                // The value matches the format; will be parsed and displayed using that format.
                format: ' Y-m-d',
                value: new Date()
            },
            {
                xtype: 'textfield',
                hidden: true,
                id: 'username_key',
                listeners: {

                    "specialkey": function (field, e) {

                        if (e.keyCode == 13) {
                            var username = field.getValue().replace(/\s+/g, "");
                            var mapower = Ext.getCmp('mapownersearchcomb').getValue();
                            missionstore.proxy.extraParams.mapower = mapower;
                            missionstore.proxy.extraParams.bgtm = Ext.getCmp('bgtmdate').getValue();
                            var edtm = Ext.getCmp('edtmdate').getValue();
                            edtm.setDate(edtm.getDate() + 1);
                            missionstore.proxy.extraParams.edtm = edtm;
                            if (username != "") {
                                missionstore.proxy.extraParams.username = username;
                            } else {
                                missionstore.proxy.extraParams.username = "";
                            }
                            missionstore.loadPage(1);

                        }

                    }

                },
                emptyText: '输入用户名'

            },
            '->',
            Ext.create('Ext.Button', {
                text: '删除任务',
                id: 'deltaskbtn',
                disabled: true,
                renderTo: Ext.getBody(),
                handler: function () {

                    //Ext.getCmp('deltaskbtn').disable();
                    var select_arr = missionformPanel.getSelectionModel().getSelection();

                    Ext.MessageBox.confirm('提示确认', '确定要删除所选任务么?', function (btn) {
                        if (btn === 'yes') {
                            Ext.Ajax.request({
                                url: 'tiletodb',
                                params: {
                                    taskid: select_arr[0].data.taskid,
                                    type: 'del'
                                },
                                success: function (response, option) {
                                    var rep_obj = Ext.JSON.decode(response.responseText);
                                    if (rep_obj.success) {
                                        Ext.MessageBox.alert('成功!', '查看状态列表！', function (btn) {

                                            missionstore.loadPage(1);

                                        });
                                    }


                                }
                            });
                        }

                    });




                }
            }),
            Ext.create('Ext.Button', {
                text: '取消任务',
                id: 'cancelbtn',
                disabled: true,
                renderTo: Ext.getBody(),
                handler: function () {

                    var select_arr = missionformPanel.getSelectionModel().getSelection();
                    Ext.Ajax.request({
                        url: 'tiletodb',
                        params: {
                            taskid: select_arr[0].data.taskid,
                            type: 'cancel'
                        },
                        success: function (response, option) {
                            var rep_obj = Ext.JSON.decode(response.responseText);
                            if (rep_obj.success) {
                                Ext.MessageBox.alert('成功!', '查看状态列表！', function (btn) {

                                    missionstore.loadPage(1);

                                });
                            }

                        }
                    });

                    //alert('取消任务!');
                }
            }),
            Ext.create('Ext.Button', {
                text: '完成失败任务',
                id: 'refinishbtn',
                disabled: true,
                renderTo: Ext.getBody(),
                handler: function () {
                    var select_arr = missionformPanel.getSelectionModel().getSelection();
                    Ext.TaskManager.start(IsHaveSession);
                    Ext.Ajax.request({
                        url: 'tiletodb',
                        params: {
                            taskid: select_arr[0].data.taskid
                        },
                        success: function (response, option) {

                            /**
                             var rep_obj = Ext.JSON.decode(response.responseText);
                             if (rep_obj.success) {
                              Ext.MessageBox.alert('成功!', '查看状态列表！', function (btn) {
                                  
                              missionstore.loadPage(1);
                                  
                              });
                          }**/


                        }
                    });


                }
            })
        ],
        // paging bar on the bottom
        bbar: Ext.create('Ext.PagingToolbar', {
            store: missionstore,
            displayInfo: true,
            displayMsg: '显示任务状态 {0} - {1} of {2}',
            emptyMsg: "没有找到符合条件的数据",
            items: [

            ]
        })
        //renderTo: 'topic-grid'
    });

    missionformPanel.getSelectionModel().on('selectionchange', function (sm, selectedRecord) {
        if (selectedRecord.length) {
            Ext.getCmp('cancelbtn').disable();

            Ext.getCmp('refinishbtn').disable();

            Ext.getCmp('deltaskbtn').disable();

            if (selectedRecord[0].data.state == 1) {
                Ext.getCmp('cancelbtn').enable();
            }
            else if (selectedRecord[0].data.state == 0) {
                Ext.getCmp('refinishbtn').enable();
                Ext.getCmp('deltaskbtn').enable();
            }
            else if (selectedRecord[0].data.state == 2) {
                Ext.getCmp('deltaskbtn').enable();
            }
        }
    });

    missionformPanel.addListener('itemdblclick', function (grid, record, e) {
        // console.log(record.data);
        var extent = new OpenLayers.Bounds(record.data.miny, record.data.minx, record.data.maxy, record.data.maxx);
        var westPanel = Ext.getCmp('west-panel');
        var treepanel = Ext.getCmp('maptree');

        var dbnode = {children: []}
        var ispublic = false;
        Ext.each(treepanel.getRootNode().childNodes, function (child) {
            if (child.raw['name'] == record.data.mapower) {
                dbnode.name = record.data.mapower;
                dbnode.text = child.raw['text'];
                dbnode.expanded = true;
                dbnode.treelevel = 0;
                dbnode.mapower = record.data.mapower;
                //dbnode.layer=record.data.layer;
                //console.log(record.data);

                Ext.each(child.childNodes, function (leaf, index) {
                    if (record.data.mapid == leaf.raw.layer) {
                        leaf.raw.checked = true;
                        ispublic = true;

                    } else {
                        leaf.raw.checked = false;
                    }
                    //console.log(record.data.layer+","+leaf.raw.layer);

                });

                if (ispublic) {
                    Ext.each(child.childNodes, function (leaf, index) {
                        dbnode.children.push(leaf.raw);
                        if (leaf.raw.checked) {
                            var layerindex = leaf.parentNode.raw['size'] - 1 - index;
                            initlayers(leaf.raw['mapower'], leaf.raw['maplabel'], leaf.raw['layer'], layerindex, true);
                        }
                        else {
                            initlayers(leaf.raw['mapower'], leaf.raw['maplabel'], leaf.raw['layer'], layerindex, false);

                        }

                    });

                    child.remove(true);
                    treepanel.getRootNode().insertBefore(dbnode, treepanel.getRootNode().childNodes[0]);

                    Ext.each(treepanel.getRootNode().childNodes, function (treenode, index) {

                        if (index == 0) {
                            Ext.getCmp('westpanelhandler').enable();
                            treenode.set('text', treenode.raw['text'] + "(--当前浏览地图)");
                            if (treenode.raw['name'] == 'tianditu') {
                                Ext.get('map').setDisplayed(true);
                                Ext.get('arcgismap').setDisplayed(false);
                                Ext.get('googlemap').setDisplayed(false);

                            } else if (treenode.raw['name'] == 'arcgisbundle') {
                                Ext.getCmp('westpanelhandler').disable();
                                Ext.get('map').setDisplayed(false);
                                Ext.get('googlemap').setDisplayed(false);
                                Ext.get('arcgismap').setDisplayed(true);

                            } else if (treenode.raw['name'] == 'google') {
                                Ext.get('map').setDisplayed(false);
                                Ext.get('googlemap').setDisplayed(true);
                                Ext.get('arcgismap').setDisplayed(false);

                            }
                        }

                        else {
                            treenode.set('text', treenode.raw['text']);
                        }


                    });

                }


                // treepanel.getRootNode().childNodes[0].set('text', treepanel.getRootNode().childNodes[0].raw.text+"(--当前浏览地图)");

                //child.set('text',child.raw['text']+"(--当前浏览地图)");
                //Ext.get('map').setDisplayed(true);
                //Ext.get('arcgismap').setDisplayed(false);
                //Ext.get('googlemap').setDisplayed(false);

            }

        });


        if (ispublic) {
            westPanel.items.items[0].expand();
            map.zoomToExtent(extent);
            map.zoomTo(record.data.layerlevel - 1);

        } else {

            Ext.MessageBox.alert('图层未发布!');
        }

    }, this);

    missionstore.loadPage(1);


    var serverparamsformPanel = Ext.widget('form', {
        title: '服务参数配置',
        frame: true,
        hidden: true,
        id: 'serverparamsform',

        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        border: false,
        bodyPadding: 10,

        fieldDefaults: {
            labelAlign: 'top',
            labelWidth: 100,
            labelStyle: 'font-weight:bold'
        },
        items: [
            {
                xtype: 'fieldset',
                title: '数据库连接信息',
                defaultType: 'textfield',
                layout: 'anchor',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        items: [
                            {
                                xtype: 'textfield',
                                fieldLabel: '连接地址',
                                name: 'pgdburl',
                                listeners: {
                                    change: function (field) {
                                    }
                                },
                                // width: 160,
                                allowBlank: false,
                                //maxLength: 10,
                                flex: 1,
                                allowBlank: false,
                                blankText: "不能为空，请填写"
                            },
                            {
                                xtype: 'textfield',
                                fieldLabel: '用户名',
                                name: 'pgdbusername',
                                listeners: {
                                    change: function (field) {
                                    }
                                },
                                width: 160,
                                allowBlank: false,
                                //maxLength: 10,
                                //flex:1,
                                allowBlank: false,
                                blankText: "不能为空，请填写"
                            },
                            {
                                xtype: 'textfield',
                                fieldLabel: '密码',
                                name: 'pgdbpassword',
                                listeners: {
                                    change: function (field) {
                                    }
                                },
                                width: 160,
                                allowBlank: false,
                                //maxLength: 10,
                                //flex:1,
                                allowBlank: false,
                                blankText: "不能为空，请填写"
                            },
                            {
                                xtype: 'button',
                                width: 80,
                                arrowAlign: 'bottom',
                                scale: 'small',
                                margin: '21 0 0 0',
                                text: '测试连接',
                                handler: function (obj) {

                                    Ext.Ajax.request({
                                        url: 'dbconected',
                                        params: {
                                            url: obj.previousSibling().previousSibling().previousSibling().getValue(),
                                            username: obj.previousSibling().previousSibling().getValue(),
                                            password: obj.previousSibling().getValue()
                                        },
                                        success: function (response, option) {
                                            var rep_obj = Ext.JSON.decode(response.responseText);
                                            if (rep_obj.success) {
                                                Ext.MessageBox.alert('成功!', '数据库连接成功！', function (btn) {
                                                });
                                            }
                                            else {
                                                Ext.MessageBox.alert('失败!', '数据库连接失败！', function (btn) {
                                                });

                                            }

                                        }
                                    });

                                }

                            }


                        ]
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: '服务参数',
                defaultType: 'textfield',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },

                items: [
                    {
                        xtype: 'textfield',
                        fieldLabel: '更新时间',
                        name: 'mapupdateTime',
                        allowBlank: false
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: '抓取线程数',
                        name: 'threadnum',
                        allowBlank: false
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: '其它',

                defaultType: 'textfield',

                layout: {
                    type: 'fit',
                    align: 'stretch'
                },

                items: [
                    {
                        xtype: 'textareafield',
                        fieldLabel: '天地图服务地址',
                        labelAlign: 'top',
                        hidden:true,
                        //flex: 1,
                        name: 'tiandituserver',
                        margins: '0',
                        allowBlank: false
                    },{
                        xtype: 'textfield',
                        fieldLabel: '缓存目录',
                        name: 'cahcedir',
                        allowBlank: false
                    }
                /**     ,
                 {
                xtype: 'radiogroup',
                fieldLabel: '默认显示地图服务',
                columns: 2,
                defaults: {
                    name: 'defaultmapserver' //Each radio has the same name so the browser will make sure only one is checked at once
                },
                items: [{
                    inputValue: 'tianditu',
                    boxLabel: '天地图'
                }, {
                    inputValue: 'arcgis',
                    boxLabel: 'arcgis缓存切片'
                }]
            }    **/
                ]
            }
        ],


        buttons: [
            {
                text: '保存修改',
                width: 150,
                handler: function () {
                    var form = this.up('form').getForm();
                    if (form.isValid()) {
                        form.submit({
                            //waitTitle: '提示', //标题
                            //waitMsg: '正在保存数据请稍后...', //提示信息                    
                            url: 'mapconfig',
                            params: {
                                type: 'save'
                            },
                            method: "POST",
                            success: function (form, action) {

                                Ext.Msg.alert("成功", "修改参数成功");

                            },
                            failure: function (form, action) {
                                Ext.MessageBox.show({
                                    title: '提示',
                                    width: 250,
                                    msg: '修改配置信息出错'
                                });
                            }
                        })


                    }
                }
            }
        ]

    });

    serverparamsformPanel.getForm().load({
        url: 'mapconfig',
        params: {
            type: "read"
        },
        success: function (form, action) {


        },
        failure: function (form, action) {
            Ext.MessageBox.show({
                title: '提示',
                width: 250,
                msg: '加载配置信息出错'
            });
            //Ext.Msg.alert("Load failed", action.result.errorMessage);
        }
    });

    var downpicgridPanel = Ext.create('Ext.grid.Panel', {
        //width: 700,
        //height: 500,
        title: '任务状态列表',
        store: downmissionstore,
        id: 'downpicPanel',
        //disableSelection: true,

        viewConfig: {
            id: 'gv1',
            trackOver: false,
            loadMask: false,
            stripeRows: false
        },
        // grid columns
        columns: [
            {
                // id: 'taskid',
                text: "地图名称",
                dataIndex: 'mapname',
                //flex: 1,
                //renderer: renderTopic,
                sortable: false
            },
            {
                text: "数据图层",
                dataIndex: 'layername',
                width: 160,
                hidden: true,
                sortable: true
            },
            {
                text: "图层级别",
                dataIndex: 'layerlevel',
                width: 70,
                align: 'center'
            },
            {
                text: "最小纬度",
                dataIndex: 'miny',
                xtype: "numbercolumn",
                format: '0.000',
                flex: 1,
                align: 'center'
            },
            {
                text: "最大纬度",
                dataIndex: 'maxy',
                xtype: "numbercolumn",
                format: '0.000',
                width: 70,
                flex: 1,
                align: 'center'
            },
            {
                text: "最小经度",
                dataIndex: 'minx',
                flex: 1,
                xtype: "numbercolumn",
                format: '0.000',
                align: 'center'
            },
            {
                text: "最大经度",
                xtype: "numbercolumn",
                format: '0.000',
                dataIndex: 'maxx',
                flex: 1,
                align: 'center'
            },
            {
                //id: 'last',
                text: "任务状态",
                dataIndex: 'status',
                width: 100,
                renderer: function (val, metaData, record) {
                    if (val == 2) {
                        return '<font color="green">已完成</font>';
                    } else if (val == 1) {
                        return '<font color="blue">正在进行</font>';
                    }
                    else if (val == 0) {
                        return '<font color="red">失败</font>';
                    }
                    else if (val == -1) {

                        return '<font color="black">正在初始化</font>';
                    }
                }

            },
            {
                text: "时间",
                dataIndex: 'updatetime',
                width: 150,
                renderer: function (val, obj, record) {
                    if (record.data.state == 1) {
                        return "";
                    }
                    var time = new Date(val);
                    val = Ext.util.Format.date(time, 'Y-m-d H:i');
                    return val;
                }

            },
            {
                text: '下载',
                flex: 1,
                align: 'center',
                hidden: false,
                renderer: function renderChoose(value, p, r) {

                    if (r.data.status == 1) {
                        return '--';
                    }
                    else if (r.data.status == 2) {

                        return '<a href="ajax/downimg.jsp?taskid=' + r.data.taskid + '&name=' + r.data.mapname
                            + '_图层级别' + r.data.layerlevel + '_范围(' + r.data.minx + '_' + r.data.miny + '_' + r.data.maxx + '_'
                            + r.data.maxy + ')" >下载</a>';

                    }


                }


            }

        ],
        tbar: [
//        {
//            text:"已完成",
//            pressed:true,
//            enableToggle:true,
//            toggleGroup:1,
//            handler:function () {
//                missionstore.proxy.extraParams.username=Ext.getCmp('username_key').getValue();
//                missionstore.proxy.extraParams.bgtm=Ext.getCmp('bgtmdate').getValue();
//                var edtm=Ext.getCmp('edtmdate').getValue();
//                edtm.setDate(edtm.getDate()+1);
//                missionstore.proxy.extraParams.edtm=edtm;
//                missionstore.proxy.extraParams.state=2;
//                missionstore.loadPage(1);
//            }
//        },
//        {
//            text:"未完成",
//            enableToggle:true,
//            toggleGroup:1,
//            handler:function () {
//                missionstore.proxy.extraParams.username=Ext.getCmp('username_key').getValue();
//                missionstore.proxy.extraParams.bgtm=Ext.getCmp('bgtmdate').getValue();
//                var edtm=Ext.getCmp('edtmdate').getValue();
//                edtm.setDate(edtm.getDate()+1);
//                missionstore.proxy.extraParams.edtm=edtm;
//                missionstore.proxy.extraParams.state='0,1,-1';
//                missionstore.loadPage(1);
//                Ext.TaskManager.start(IsHaveSession);                
//            }
//        },
//        {
//            xtype: 'button',
//            text: '<span style="font-family:宋体;font-weight:bold">>></span>',
//            tooltip:'显示/隐藏内容',
//            //id: 'westpanelhandler',
//            handler: function () {
//                if(this.nextSibling().isHidden()){
//                    this.nextSibling().show();
//                    this.nextSibling().nextSibling().show();
//                    this.nextSibling().nextSibling().nextSibling().show();
//                    this.setText('<span style="font-family:宋体;font-weight:bold"><<</span>');
//                }else{
//                    this.nextSibling().hide();
//                    this.nextSibling().nextSibling().hide();
//                    this.nextSibling().nextSibling().nextSibling().hide();
//                    this.setText('<span style="font-family:宋体;font-weight:bold">>></span>');
//                }
//                                
//            }
//        },
//        {
//            xtype: 'datefield',
//            labelWidth :80,
//            labelAlign:'right',
//            fieldLabel: '起始日期',
//            name: 'bgtm',
//            id:'bgtmdate',
//            hidden:true,
//            // The value matches the format; will be parsed and displayed using that format.
//            format: 'Y-m-d',
//            value: '2012-12-21'
//        }, {
//            xtype: 'datefield',
//            //anchor: '100%',
//            hidden:true,
//            id:'edtmdate',
//            labelWidth :80,
//            labelAlign:'right',
//            fieldLabel: '结束日期',
//            name: 'edtm',
//            // The value matches the format; will be parsed and displayed using that format.
//            format: ' Y-m-d',
//            value: new Date()
//        },
//        {
//            xtype: 'textfield',
//            hidden:true,
//            id: 'username_key',
//            listeners:{
//
//                "specialkey": function(field,e){
//
//                    if (e.keyCode == 13) {
//                        var username=field.getValue().replace(/\s+/g,"");
//                        missionstore.proxy.extraParams.bgtm=Ext.getCmp('bgtmdate').getValue();
//                        var edtm=Ext.getCmp('edtmdate').getValue();
//                        edtm.setDate(edtm.getDate()+1);
//                        missionstore.proxy.extraParams.edtm=edtm;
//                        if(username!=""){
//                            missionstore.proxy.extraParams.username=username;
//                        }else{
//                            missionstore.proxy.extraParams.username="";
//                        }
//                        missionstore.loadPage(1);
//
//                    }
//
//                }
//
//            },
//            emptyText: '输入用户名'
//	            	  
//        },
            '->', Ext.create('Ext.Button', {
                text: '删除任务',
                id: 'downdeltaskbtn',
                disabled: true,
                renderTo: Ext.getBody(),
                handler: function () {

                    //Ext.getCmp('deltaskbtn').disable();
                    var select_arr = downpicgridPanel.getSelectionModel().getSelection();

                    Ext.Ajax.request({
                        url: 'downtiles',
                        params: {
                            taskid: select_arr[0].data.taskid,
                            type: 'del'
                        },
                        success: function (response, option) {
                            var rep_obj = Ext.JSON.decode(response.responseText);
                            if (rep_obj.success) {
                                Ext.MessageBox.alert('成功!', '查看状态列表！', function (btn) {

                                    downmissionstore.loadPage(1);

                                });
                            }


                        }
                    });

                }
            })
        ],
        // paging bar on the bottom
        bbar: Ext.create('Ext.PagingToolbar', {
            store: downmissionstore,
            displayInfo: true,
            displayMsg: '显示任务状态 {0} - {1} of {2}',
            emptyMsg: "没有找到符合条件的数据",
            items: [

            ]
        })
        //renderTo: 'topic-grid'
    });

    downpicgridPanel.getSelectionModel().on('selectionchange', function (sm, selectedRecord) {
        if (selectedRecord.length) {

            Ext.getCmp('downdeltaskbtn').disable();

            if (selectedRecord[0].data.state == 1) {
                //Ext.getCmp('cancelbtn').enable();
            }
            else if (selectedRecord[0].data.state == 0) {
                // Ext.getCmp('refinishbtn').enable();
                Ext.getCmp('downdeltaskbtn').enable();
            }
            else if (selectedRecord[0].data.state == 2) {
                Ext.getCmp('downdeltaskbtn').enable();
            }
        }
    });

    downpicgridPanel.addListener('itemdblclick', function (grid, record, e) {

        var extent = new OpenLayers.Bounds(record.data.miny, record.data.minx, record.data.maxy, record.data.maxx);
        var westPanel = Ext.getCmp('west-panel');
        westPanel.items.items[0].expand();
        map.zoomToExtent(extent);
        map.zoomTo(record.data.layerlevel - 1);
    }, this);

    downmissionstore.loadPage(1);


    var makecacheformPanel = Ext.widget('form', {
        title: '抓取瓦片缓存数据配置',
        frame: true,
        id: 'makecacheform',

        //width: 550,
        //height:500,
        bodyPadding: 15,
        fieldDefaults: {
            labelAlign: 'right',
            labelWidth: 90,
            msgTarget: 'qtip'
        },

        items: [
            // Contact info
            {
                xtype: 'fieldset',
                title: '地图服务及图层选择',
                defaultType: 'textfield',
                layout: 'anchor',


                defaults: {
                    anchor: '100%'
                },
                items: [
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        defaultType: 'textfield',
                        margin: '0 0 10 0',
                        items: [
                            {
                                fieldLabel: '地图选择',
                                name: 'mapowner',
                                xtype: 'combo',
                                allowBlank: false,
                                blankText: "不能为空，请选择",
                                displayField: 'text',
                                valueField: 'name',
                                emptyText: '请选择地图服务',
                                listeners: {
                                    scope: this,
                                    'select': function (combo, records) {
                                        combo.next().enable();
                                        maplayerstore.getProxy().extraParams = {
                                            mapower: combo.getValue()
                                        };
                                        maplayerstore.load();
                                    }
                                },
                                queryMode: 'local',
                                flex: 1,
                                store: mapserverstore

                            },
                            {
                                fieldLabel: '图层选择',
                                name: 'maplayer',
                                xtype: 'combo',
                                displayField: 'text',
                                valueField: 'layer',
                                emptyText: '请选择地图服务',
                                allowBlank: false,
                                blankText: "不能为空，请选择",
                                //margins: '6 6 6 6',
                                disabled: true,
                                listeners: {
                                    scope: this,
                                    'select': function (combo, records) {
                                        layerdatastore.getProxy().extraParams = {
                                            layerdata: combo.getValue()
                                        };
                                        layerdatastore.load();
                                        layerdatacombo.enable();
                                    }
                                },
                                queryMode: 'local',
                                flex: 1,
                                store: maplayerstore
                            }

                        ]
                    },
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        defaultType: 'textfield',
                        items: [layerdatacombo, levelcombo
                            //                    ,{
                            //                    xtype: 'textfield',
                            //                    id:'hidelayerlabeltext',
                            //                    hidden:true,
                            //                    fieldLabel: '最小经度',
                            //                    name: 'layerlabel',
                            //                width: 160,
                            //                allowBlank: false,
                            //                maxLength: 10,
                            //                flex:1,
                            //                allowBlank:false,
                            //                blankText:"不能为空，请填写"
                            //                
                            //            }
                        ]
                    }

                ]
            },
            {
                xtype: 'fieldset',
                title: '经纬度范围',
                defaultType: 'textfield',
                layout: 'anchor',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        items: [
                            {
                                xtype: 'textfield',
                                fieldLabel: '最小经度',
                                name: 'minx',
                                id: 'minxtext',
                                listeners: {
                                    change: function (field) {
                                        //Ext.getCmp('maxxtext').validator();

                                    }
                                },
                                validator: function () {
                                    if (Ext.getCmp('maxxtext').getValue() != "") {
                                        if (this.getValue() < Ext.getCmp('maxxtext').getValue()) {
                                            return true;
                                        } else {
                                            return "最大经度必须大于最小经度!";
                                        }


                                    }
                                    else {

                                        return true;
                                    }

                                },
                                width: 160,
                                allowBlank: false,
                                //maxLength: 10,
                                flex: 1,
                                allowBlank: false,
                                blankText: "不能为空，请填写",

                                //enforceMaxLength: true,
                                maskRe: /[\d\.]/,
                                regex: /^\d{3}(\.\d+)?$/,
                                regexText: '经度数值必须是 xxx or xxx.xxxx'
                            }
                            ,
                            {
                                xtype: 'textfield',
                                fieldLabel: '最大经度',
                                name: 'maxx',
                                id: 'maxxtext',
                                invalidText: '最大经度必须大于最小经度!',
                                listeners: {
                                    change: function (field) {
                                    }
                                },
                                validator: function () {
                                    if (this.getValue() > Ext.getCmp('minxtext').getValue()) {
                                        return true;
                                    } else {
                                        return "最大经度必须大于最小经度!";
                                    }

                                },
                                allowBlank: false,
                                flex: 1,
                                allowBlank: false,
                                blankText: "不能为空，请填写",

                                //enforceMaxLength: true,
                                maskRe: /[\d\.]/,
                                regex: /^\d{3}(\.\d+)?$/,
                                regexText: '经度数值必须是 xxx or xxx.xxxx'
                            },

                            {
                                xtype: 'textfield',
                                fieldLabel: '最小纬度',
                                name: 'miny',
                                id: 'minytext',
                                listeners: {
                                    change: function (field) {
                                    }
                                },
                                allowBlank: false,
                                //maxLength: 10,
                                flex: 1,
                                validator: function () {
                                    if (Ext.getCmp('maxytext').getValue() != "") {
                                        if (this.getValue() < Ext.getCmp('maxytext').getValue()) {
                                            return true;
                                        } else {
                                            return "最大经度必须大于最小经度!";
                                        }


                                    }
                                    else {

                                        return true;
                                    }

                                },
                                allowBlank: false,
                                blankText: "不能为空，请填写",
                                //enforceMaxLength: true,
                                maskRe: /[\d\.]/,
                                regex: /^\d{2}(\.\d+)?$/,
                                regexText: '经度数值必须是 xx or xx.xxxx'
                            },
                            {
                                xtype: 'textfield',
                                fieldLabel: '最大纬度',
                                name: 'maxy',
                                id: 'maxytext',
                                invalidText: '最大纬度必须大于最小纬度!',
                                validator: function () {
                                    if (this.getValue() > Ext.getCmp('minytext').getValue()) {
                                        return true;
                                    } else {
                                        return false;
                                    }

                                },
                                listeners: {
                                    change: function (field) {

                                    }
                                },
                                allowBlank: false,
                                //maxLength: 10,
                                flex: 1,
                                allowBlank: false,
                                blankText: "不能为空，请填写",
                                //enforceMaxLength: true,
                                maskRe: /[\d\.]/,
                                regex: /^\d{2}(\.\d+)?$/,
                                regexText: '经度数值必须是 xx or xx.xxxx'
                            }


                        ]
                    }
                ]
            },

            {
                xtype: 'fieldset',
                title: '其它',
                defaultType: 'textfield',
                layout: 'anchor',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        items: [
                            {
                                xtype: 'textfield',
                                fieldLabel: '用户',
                                //defaultValue:'jack',
                                value: userobj.username,
                                name: 'username',
                                listeners: {
                                    change: function (field) {
                                    }
                                },
                                width: 160,
                                allowBlank: false,
                                //maxLength: 10,
                                // flex:1,
                                allowBlank: false,
                                blankText: "不能为空，请填写"
                            },
                            {
                                xtype: 'radiogroup',
                                fieldLabel: '是否覆盖',
                                columns: 2,

                                labelWidth: 200,
                                labelAlign: 'right',
                                width: 360,
                                defaults: {
                                    name: 'isenforce' //Each radio has the same name so the browser will make sure only one is checked at once
                                },
                                items: [
                                    {
                                        inputValue: 'false',
                                        width: 80,
                                        checked: true,
                                        boxLabel: '不覆盖'
                                    },
                                    {
                                        inputValue: 'true',
                                        width: 80,
                                        boxLabel: '覆盖'
                                    }
                                ]
                            },
                            {
                                xtype: 'radiogroup',
                                fieldLabel: '是否生成缓存数据',
                                columns: 2,
                                hidden: true,
                                labelWidth: 200,
                                labelAlign: 'right',
                                width: 300,
                                defaults: {
                                    name: 'iscache' //Each radio has the same name so the browser will make sure only one is checked at once
                                },
                                items: [
                                    {
                                        inputValue: 'false',
                                        width: 80,
                                        checked: true,
                                        boxLabel: '是'
                                    },
                                    {
                                        inputValue: 'true',
                                        width: 80,
                                        boxLabel: '否'
                                    }
                                ]
                            }

                        ]
                    }
                ]
            }


        ],

        buttons: [
            {
                text: '重置',
                handler: function () {
                    this.up('form').getForm().reset();
                }
            },
            {
                text: '抓取切片',
                width: 150,
                handler: function () {

                    var form = this.up('form').getForm();
                    if (form.isValid()) {

                        form.submit({
                            //waitTitle: '提示', //标题
                            //waitMsg: '正在保存数据请稍后...', //提示信息                    
                            url: 'tiletodb',
                            //url:'ajax/test.jsp',

                            method: "POST",
                            success: function (form, action) {

                                var feedpanel = Ext.getCmp('feedpanel');
                                feedpanel.view.getSelectionModel().select(feedpanel.view.store.getAt(0));

                                var top_btn = missionformPanel.getDockedItems()[1].items.items[1];
                                top_btn.toggle();
                                top_btn.handler();

                            },
                            failure: function (form, action) {

                            }
                        })

                    }
                }
            },
            {
                text: '下载图片',
                width: 150,
                handler: function () {


                    var form = this.up('form').getForm();
                    var tiles_num = 0;
                    var layerlevel = [];
                    var faillevel = [];
                    if (form.getValues().mapowner === 'tianditu') {

                        Ext.each(form.getValues().layerlevel, function (a) {
                            var tilelon = map.getResolutionForZoom(parseInt(a)) * 256;

                            var offsetlon = form.getValues().minx - (-180);
                            var tilecol = Math.floor(offsetlon / tilelon);
                            var tileoffsetlon = (-180) + tilecol * tilelon;

                            var offsetlat = form.getValues().maxy - (-90 + tilelon);
                            var tilerow = Math.ceil(offsetlat / tilelon);
                            var tileoffsetlat = -90 + tilerow * tilelon;

                            //var tilelat =  map.getResolutionForZoom(a+1)  * 256;
                            tiles_num = Math.floor((form.getValues().maxx - tileoffsetlon) / tilelon) * Math.floor((tileoffsetlat - form.getValues().miny) / tilelon);
                            if (tiles_num < limittiles_num) {
                                layerlevel.push(a);
                            } else {
                                faillevel.push(a);

                            }

                        });


                    } else if (form.getValues().mapowner === 'google') {

                        Ext.each(form.getValues().layerlevel, function (a) {
                            var mercatormin = lonlat2mercator(form.getValues().minx, form.getValues().miny);
                            var mercatormax = lonlat2mercator(form.getValues().maxx, form.getValues().maxy);


                            var mercatorminx = mercatormin[0];
                            var mercatormaxx = mercatormax[0];

                            var mercatorminy = mercatormin[1];
                            var mercatormaxy = mercatormax[1];


                            //int taskid = Integer.parseInt(params.get("taskid").toString());
                            //int layerid=(Integer) params.get("layerid");

                            //long tileoffsetx=0;
                            //int startx=(int) ( ( (minx +180) / 360 * (1 << layerlevel) ) + 1.5);
                            var resolution = 156543.0339 / Math.pow(2, a);
                            var startx = Math.round((mercatorminx - (-20037508.34)) / (resolution * 256));

                            var maxx_num = Math.round((mercatormaxx - (-20037508.34)) / (resolution * 256));

                            var tileoffsety = Math.round((20037508.34 - mercatormaxy) / (resolution * 256));

                            var maxy_num = Math.round((20037508.34 - mercatorminy) / (resolution * 256));


                            tiles_num = (maxy_num - tileoffsety + 1) * (maxx_num - startx + 1);
                            if (tiles_num < limittiles_num) {
                                layerlevel.push(a);
                            } else {
                                faillevel.push(a);

                            }

                        });


                    }
                    //console.log(tiles_num);
                    if (layerlevel.length == 0) {
                        Ext.MessageBox.alert('任务失败', '数据量太大,请选择较小范围或层级');

                        return;

                    }
                    else if (faillevel.length > 0) {

                        Ext.MessageBox.alert('提示', '层级:' + faillevel.join(",") + ' 数据量太大,请选择较小范围或层级');
                        form.setValues({"layerlevel": layerlevel});
                    }


                    if (form.isValid()) {

                        form.submit({
                            //waitTitle: '提示', //标题
                            //waitMsg: '正在保存数据请稍后...', //提示信息                    
                            url: 'downtiles',
                            //url:'ajax/test.jsp',

                            method: "POST",
                            success: function (form, action) {

                                var feedpanel = Ext.getCmp('feedpanel');
                                feedpanel.view.getSelectionModel().select(feedpanel.view.store.getAt(1));
                                downmissionstore.loadPage(1);
                                Ext.TaskManager.start(downHaveSession);
                                //downmissionstore.loadPage(1);

                                /**
                                 var top_btn=missionformPanel.getDockedItems()[1].items.items[1];
                                 top_btn.toggle();
                                 top_btn.handler();
                                 **/
                            },
                            failure: function (form, action) {

                            }
                        })

                    }
                }
            }
        ]

    });


    var makeserverPanel = Ext.create('widget.feedpanel', {
        //collapsible: true,
        //width: 225,
        id: 'feedpanel2',
        layout: 'fit',
        border: false,
        //floatable: false,
        title: '',
        //split: true,
        minWidth: 175,
        feeds: [
            {
                title: '服务管理',
                url: 'servermanager'
            },
            {
                title: '参数设置',
                url: 'serverparams'
            }
        ],
        listeners: {
            scope: this,
            feedselect: function (feed, title, url) {
                Ext.getCmp('mapPanel').hide();
                if (url === 'serverparams') {

                    // Ext.getCmp('cachePanel').show();

                    Ext.getCmp('mapserverPanel').hide();
                    Ext.getCmp('serverparamsform').show();
                } else if (url === 'servermanager') {

                    Ext.getCmp('serverparamsform').hide();
                    Ext.getCmp('mapserverPanel').show();
                }

            }
        }
    });


    var makecachePanel = Ext.create('widget.feedpanel', {
        //collapsible: true,
        //width: 225,
        id: 'feedpanel',
        layout: 'fit',
        border: false,
        //floatable: false,
        title: '',
        //split: true,
        minWidth: 175,
        feeds: [
            {
                title: '任务管理',
                url: 'mission'
            },
            {
                title: '图片管理',
                url: 'downpic'
            },
            {
                title: '抓取切片',
                url: 'cache'
            }
        ],
        listeners: {
            scope: this,
            feedselect: function (feed, title, url) {
                Ext.getCmp('mapPanel').hide();
                if (url === 'cache') {

                    // Ext.getCmp('cachePanel').show();
                    Ext.getCmp('makecacheform').show();
                    Ext.getCmp('missionformPanel').hide();
                    Ext.getCmp('downpicPanel').hide();
                } else if (url === 'mission') {

                    Ext.getCmp('makecacheform').hide();
                    Ext.getCmp('downpicPanel').hide();
                    Ext.getCmp('missionformPanel').show();
                }
                else if (url === 'downpic') {
                    Ext.getCmp('makecacheform').hide();
                    Ext.getCmp('missionformPanel').hide();
                    Ext.getCmp('downpicPanel').show();
                }


            }
        }
    });

    var store = Ext.create('Ext.data.TreeStore', {
        proxy: {
            type: 'ajax',
            url: 'maptree?ispublic=true'
        },
        listeners: {
            load: {
                fn: function (data, root) {

                    Ext.each(root.childNodes, function (child, index) {


                        if (index == 0) {

                            var tempmap = '';
                            //console.log(child.raw);
                            if (child.raw['name'] == "tianditu")tempmap = 'map';
                            else if (child.raw['name'] == "arcgisbundle")tempmap = 'arcgismap';
                            else if (child.raw['name'] == "google")tempmap = 'googlemap';

                            // console.log(tempmap);
                            child.set('text', child.raw['text'] + "(--当前浏览地图)");
                            Ext.get(tempmap).setDisplayed(true);
                            // Ext.get('arcgismap').setDisplayed(false);
                            //Ext.get('googlemap').setDisplayed(false);

                        }
                        Ext.each(child.childNodes, function (leaf, index) {

                            if (leaf.data.checked) {
                                var layerindex = leaf.parentNode.raw['size'] - 1 - index;
                                initlayers(leaf.raw['mapower'], leaf.raw['maplabel'], leaf.raw['layer'], layerindex, true);
                            }
                            else {
                                initlayers(leaf.raw['mapower'], leaf.raw['maplabel'], leaf.raw['layer'], layerindex, false);

                            }
                        })

                    });
                }
            },
            scope: this
        }
        /**
         sorters: [{
            property: 'leaf',
            direction: 'ASC'
        }, {
            property: 'text',
            direction: 'ASC'
        }]**/
    });

    var maptree = Ext.create('Ext.tree.Panel', {
        store: store,
        id: 'maptree',
        rootVisible: false,
        border: false,
        useArrows: true,
        viewConfig: {
            plugins: {
                ptype: 'treeviewdragdrop'
            },
            listeners: {
                'beforedrop': function (node, data, overModel, dropPosition, eOpts) {

                    if (dropPosition == "append" || data.records[0].parentNode != overModel.parentNode) {
                        //dropPosition = 'after';
                        return false;
                    }

                },

                'drop': function (node, data, overModel, dropPosition, dropFunction, eOpts) {
                    //两节点不在同一父节点上，不能移动；两节点在同一父节点，且都为图层节点时，不能append
                    // testobj=node; 

                    if (dropPosition == "append") {
                        dropPosition = 'after';
                    }
                    if (data.records[0].raw['treelevel'] == 0) {

                        Ext.each(maptree.getRootNode().childNodes, function (treenode, index) {

                            if (index == 0) {
                                Ext.getCmp('westpanelhandler').enable();
                                treenode.set('text', treenode.raw['text'] + "(--当前浏览地图)");
                                if (treenode.raw['name'] == 'tianditu') {
                                    Ext.get('map').setDisplayed(true);
                                    Ext.get('arcgismap').setDisplayed(false);
                                    Ext.get('googlemap').setDisplayed(false);

                                } else if (treenode.raw['name'] == 'arcgisbundle') {
                                    Ext.getCmp('westpanelhandler').disable();
                                    Ext.get('map').setDisplayed(false);
                                    Ext.get('googlemap').setDisplayed(false);
                                    Ext.get('arcgismap').setDisplayed(true);

                                } else if (treenode.raw['name'] == 'google') {
                                    Ext.get('map').setDisplayed(false);
                                    Ext.get('googlemap').setDisplayed(true);
                                    Ext.get('arcgismap').setDisplayed(false);

                                }
                            }

                            else {
                                treenode.set('text', treenode.raw['text']);
                            }


                        });

                        /**
                         if(dropPosition == "before"){
                            if(data.records[0].raw['name']=='tianditu'){
                                Ext.get('map').setDisplayed(true);
                                Ext.get('arcgismap').setDisplayed(false);
                                Ext.get('googlemap').setDisplayed(false);
                                    
                            }else if(data.records[0].raw['name']=='arcgisbundle') {
                                Ext.get('map').setDisplayed(false);
                                Ext.get('googlemap').setDisplayed(false);
                                Ext.get('arcgismap').setDisplayed(true);
                                    
                            }else if(data.records[0].raw['name']=='google') {
                                Ext.get('map').setDisplayed(false);
                                Ext.get('googlemap').setDisplayed(true);
                                Ext.get('arcgismap').setDisplayed(false);
                                    
                            }						
                        }else if(dropPosition == "after"){
                                            
                            if(data.records[0].raw['name']=='tianditu'){
                                Ext.get('map').setDisplayed(true);
                                Ext.get('arcgismap').setDisplayed(false);
                                Ext.get('googlemap').setDisplayed(false);
                                    
                            }else if(data.records[0].raw['name']=='arcgisbundle') {
                                Ext.get('map').setDisplayed(false);
                                Ext.get('arcgismap').setDisplayed(true);
                                Ext.get('googlemap').setDisplayed(false);
                                      
                                    
                            }else if(data.records[0].raw['name']=='google') {
                                Ext.get('map').setDisplayed(false);
                                Ext.get('googlemap').setDisplayed(true);
                                Ext.get('arcgismap').setDisplayed(false);
                            }			
                                
                                   
                                
                        }
                         data.records[0].set('text',data.records[0].raw['text']+"(--当前浏览地图)");
                         overModel.set('text',overModel.raw['text']);
                         **/
                        return;

                    }

                    var tempmap = null;
                    if (data.records[0].raw['mapower'] == "tianditu")tempmap = map;
                    else if (data.records[0].raw['mapower'] == "arcgisbundle")tempmap = arcgismap;
                    else if (data.records[0].raw['mapower'] == "google")tempmap = googlemap;

                    var layer = tempmap.getLayersByName(data.records[0].raw['layer'])[0];
                    var targetLayer = tempmap.getLayersByName(overModel.raw['layer'])[0];
                    var num = tempmap.getLayerIndex(targetLayer);
                    var sel_num = tempmap.getLayerIndex(layer);

                    for (var i = 0; i < tempmap.layers.length; i++) {

                        if (i == num) {
                            tempmap.setLayerIndex(layer, i);
                        } else {
                            tempmap.setLayerIndex(tempmap.layers[i], i);
                        }
                    }
                    return;

                    if (dropPosition == "before") {
                        num = num + 1;
                    }


                    tempmap.setLayerIndex(layer, num);


                    if (layer == tempmap.baseLayer) {
                        var baselayer = tempmap.layers[0];
                        tempmap.setBaseLayer(baselayer);

                        layer.setIsBaseLayer(false);
                        layer.setVisibility(true);
                        tempmap.resetLayersZIndex();
                    }
                    if (targetLayer == tempmap.baseLayer && dropPosition == "after") {
                        var baselayer = tempmap.baseLayer;
                        tempmap.setBaseLayer(layer);

                        baselayer.setIsBaseLayer(false);
                        baselayer.setVisibility(true);
                        tempmap.resetLayersZIndex();
                    }


                }
            }
        },
        listeners: {
            'checkchange': function (node, checked) {


                var addedlayers = map.getLayersByName(node.raw['layer']);
                if(arcgismap!=null)addedlayers = addedlayers.concat(arcgismap.getLayersByName(node.raw['layer']));
                if(googlemap!=null)addedlayers = addedlayers.concat(googlemap.getLayersByName(node.raw['layer']));
                if (addedlayers.length == 0) {
                    initlayers(node.raw['mapower'], node.raw['maplabel'], node.raw['layer'], true);
                } else {

                    Ext.each(addedlayers, function (layer) {
                        layer.setVisibility(checked);
                    });
                }
            },
            'itemremove': function (obj, node, index, obj2) {

                //map.removeLayer(map.getLayersByName(node.raw['layer'])[0]);
                //map.setBaseLayer(map.layers[0]);

            },
            'itemappend': function (obj, node, index, obj2) {

                /**

                 if(node.raw['name']=='tianditu'){
                node.set('text',node.raw['text']+"(--当前浏览地图)"); 
            }
                 if(node.data.checked){
                var layerindex=node.parentNode.raw['size']-1-index;
                initlayers(node.raw['mapower'],node.raw['maplabel'],node.raw['layer'],layerindex);
            }
                 **/
            }
        },
        frame: false
    });

    maptree.on('itemcontextmenu', function (view, record, item, index, event) {
        showLayerTreeMenu(record, event);
        //console.log(record);
        event.stopEvent();

    }, this);

    function showLayerTreeMenu(node, e) {
        // testobj=node;
        if (node.isLeaf()) {
            var mapMenu = new Ext.menu.Menu({
                items: [
                    {
                        text: '显示图层地址',
                        handler: function (obj, e, f, g) {

                            var urlstr = [];
                            Ext.each(mapserverurls, function (a, index) {
                                urlstr.push(a + "?mapower=" + node.raw['mapower'] + "&maplayerid=" + node.raw['layer']);

                            });
                            Ext.MessageBox.alert('图层地址数组如下:', Ext.JSON.encode(urlstr));
                        }
                    }
                ]
            });
            mapMenu.showAt(e.getXY());
            return;


        } else {
            return;


        }


    }

    var viewport = Ext.create('Ext.Viewport', {
        id: 'border-example',
        layout: 'border',
        items: [
            // create instance immediately
            Ext.create('Ext.Component', {
                region: 'north',
                height: 55, // give north and south regions a height
                align: 'center',
                autoEl: {
                    xtype: 'container',
                    tag: 'div',
                    html: '<table width="100%" height="100%"><tr><td><img height="55px;" src="img/loginbg_03.png"></td><td width="*"></td><td  width="100px;">'
                        + '<p id="usertip"></p></td><td width="27px;"><a href="logout">注销</a></td>'
                        + '<td width="80px;"> <select name="styleswitcher_select" id="styleswitcher_select">'
                        + ' <option value="js/ext-4.1.1a/resources/css/ext-all.css">明亮主题</option>'
                        + ' <option value="js/ext-4.1.1a/resources/css/ext-all-gray.css"  selected="true">灰色主题</option>'

                        + '</select></td>'
                        + '</tr></table>'
                }
            }),
            {
                region: 'west',
                stateId: 'navigation-panel',
                id: 'west-panel', // see Ext.getCmp() below
                title: '导航菜单',
                split: true,
                width: 200,
                minWidth: 175,
                maxWidth: 400,
                collapsible: true,
                animCollapse: true,
                margins: '0 0 0 5',
                layout: 'accordion',
                items: [
                    {
                        //contentEl: 'west',
                        title: '地图浏览',
                        layout: 'fit',
                        listeners: {
                            'collapse': function (obj) {
                                Ext.getCmp('mapPanel').hide();
                            },
                            'expand': function (obj) {
                                Ext.getCmp('mapPanel').show();

                            }
                        },
                        items: [
                            maptree
                        ],
                        iconCls: 'nav' // see the HEAD section for style used
                    },
                    {
                        title: '任务管理',

                        layout: 'fit',
                        listeners: {
                            'collapse': function (obj) {
                                Ext.getCmp('cachePanel').hide();
                            },
                            'expand': function (obj) {
                                Ext.getCmp('cachePanel').show();
                                if (Ext.getCmp('feedpanel').getSelectedItem().getData().url === 'cache') {
                                    Ext.getCmp('makecacheform').show();
                                    Ext.getCmp('missionformPanel').hide();
                                    Ext.getCmp('downpicPanel').hide();
                                }
                                else if (Ext.getCmp('feedpanel').getSelectedItem().getData().url === 'mission') {

                                    Ext.getCmp('missionformPanel').show();
                                    Ext.getCmp('makecacheform').hide();
                                    Ext.getCmp('downpicPanel').hide();
                                } else if (Ext.getCmp('feedpanel').getSelectedItem().getData().url === 'downpic') {
                                    Ext.getCmp('downpicPanel').show();
                                    Ext.getCmp('missionformPanel').hide();
                                    Ext.getCmp('makecacheform').hide();

                                    //alert(2);
                                }


                            }
                        },

                        items: [
                            makecachePanel
                        ],
                        iconCls: 'settings'
                    },
                    {
                        title: '服务配置',
                        layout: 'fit',
                        listeners: {
                            'collapse': function (obj) {
                                Ext.getCmp('serverPanel').hide();
                            },
                            'expand': function (obj) {
                                Ext.getCmp('serverPanel').show();

                            }
                        },
                        items: [makeserverPanel],

                        // html: '<p>设置一些参数.</p>',
                        iconCls: 'info'
                    }
                ]
            }, {
                region: 'center',
                id: 'centerregion',
                layout: 'fit',
                items: [
                    {
                        xtype: 'panel',
                        hidden: false,
                        region: 'center',
                        layout: {
                            type: 'fit'

                        },
                        id: 'mapPanel',
                        style: {

                            border: 'none'
                        },
                        bodyStyle: {
                            borderColor: 'gray',
                            borderWidth: '1px',
                            borderTop: 'none'
                        },
                        title: '',
                        tbar: {
                            height: 30,
                            style: {
                                borderTop: 'none',
                                borderLeft: 'none',
                                borderRight: 'none',
                                borderBottom: '2px solid #009ACC'
                            },

                            layout: {

                                overflowHandler: 'Menu'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    text: '框选地图抓取切片',
                                    id: 'westpanelhandler',
                                    style: {
                                        fontColor: '#009ACC'
                                    },
                                    handler: function () {
                                        if (box_control == null) {
                                            box_control = new OpenLayers.Control();
                                            var tempmap = null;
                                            if (Ext.get('map').isDisplayed())tempmap = map;
                                            else if (Ext.get('arcgismap').isDisplayed())tempmap = arcgismap;
                                            else if (Ext.get('googlemap').isDisplayed())tempmap = googlemap;
                                            OpenLayers.Util.extend(box_control, {
                                                draw: function () {
                                                    this.box = new OpenLayers.Handler.Box(box_control, {
                                                        "done": this.notice
                                                    }, {
                                                        keyMask: OpenLayers.Handler.MOD_NONE
                                                    });
                                                    this.box.activate();
                                                },

                                                notice: function (bounds) {

                                                    lonlat_start = tempmap.getLonLatFromViewPortPx(new OpenLayers.Pixel(
                                                        bounds.left, bounds.top));
                                                    lonlat_end = tempmap.getLonLatFromViewPortPx(new OpenLayers.Pixel(
                                                        bounds.right, bounds.bottom));
                                                    //markers.clearMarkers();
                                                    if (tempmap.getProjection() == "EPSG:900913") {
                                                        lonlat_start.transform('EPSG:3857', 'EPSG:4326');
                                                        lonlat_end.transform('EPSG:3857', 'EPSG:4326');

                                                    }
                                                    start_x = lonlat_start.lon;
                                                    start_y = lonlat_start.lat;
                                                    end_x = lonlat_end.lon;
                                                    end_y = lonlat_end.lat;

                                                    var westPanel = Ext.getCmp('west-panel');
                                                    var feedpanel = Ext.getCmp('feedpanel');
                                                    feedpanel.initnum = 2;
                                                    westPanel.items.items[1].expand();

                                                    feedpanel.view.getSelectionModel().select(feedpanel.view.store.getAt(2));

                                                    var record = {
                                                        data: {
                                                            username: userobj.username,
                                                            minx: start_x,
                                                            miny: end_y,
                                                            maxx: end_x,
                                                            maxy: start_y
                                                        }
                                                    };
                                                    Ext.getCmp('makecacheform').getForm().loadRecord(record);


                                                }
                                            });


                                            tempmap.addControl(box_control);


                                        }
                                        box_control.box.activate();
                                        box_control.activate();

                                        Ext.getCmp('mapstatues').setText('当前地图状态:框选地图');

                                    }
                                },
                                '->',
                                {
                                    xtype: 'label',
                                    text: '当前地图状态:地图漫游',
                                    id: 'mapstatues',
                                    style: 'color: blue; font-size: 11px'
                                }
                                ,
                                '->',
                                {
                                    xtype: 'button',
                                    text: '漫游',
                                    handler: function () {
                                        Ext.getCmp('mapstatues').setText('当前地图状态:地图漫游');
                                        if (box_control != null) {
                                            box_control.deactivate();
                                            box_control.box.deactivate();
                                        }

                                    }
                                    //style: 'color: #0ee0f0; font-weight: bold; font-size: 11px'
                                }

                            ]
                        },
                        html: '<div id="map" style="width:100%;height:100%;border:0px solid black;"></div>\n\
                        <div id="arcgismap" style="width:100%;height:100%;border:0px solid black;"></div>\n\
                            <div id="googlemap" style="width:100%;height:100%;border:0px solid black;"></div> '
                    },
                    {
                        xtype: 'panel',
                        hidden: true,
                        layout: 'fit',
                        region: 'center',
                        id: 'cachePanel',
                        style: {
                            border: 'none'
                        },
                        bodyStyle: {
                            borderColor: 'gray',
                            borderWidth: '1px',
                            borderTop: 'none'
                        },
                        title: '',
                        items: [makecacheformPanel, missionformPanel, downpicgridPanel]
                    },
                    {
                        xtype: 'panel',
                        hidden: true,
                        layout: 'fit',
                        region: 'center',
                        id: 'serverPanel',
                        style: {
                            border: 'none'
                        },
                        bodyStyle: {
                            borderColor: 'gray',
                            borderWidth: '1px',
                            borderTop: 'none'
                        },
                        title: '',
                        items: [mapserverForm, serverparamsformPanel]
                    }

                ]

            }
        ]
    });

    /*
     **转换样式风格
     */
    Ext.get('styleswitcher_select').on('change', function (e, select) {
        var name = select[select.selectedIndex].value;
        Ext.util.CSS.swapStyleSheet('theme', name);
    });

    var time = new Date(parseInt(userobj.logintime));
    //console.log(time);
    var val = Ext.util.Format.date(time, 'Y-m-d H:i');
    var usertip = Ext.create('Ext.tip.ToolTip', {
        target: 'usertip',
        title: '您好,' + userobj.username + '!',
        width: 200,
        html: '上次登录时间:' + val,
        autoHide: false,
        closable: true,
        draggable: true
    });

    usertip.showAt([Ext.get('usertip').getX() - 110, 0]);

}