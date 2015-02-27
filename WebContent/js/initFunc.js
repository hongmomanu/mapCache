/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


 Ext.require(['*']);
 Ext.onReady(function() {
    
     Ext.QuickTips.init();
     Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));
     
     initFeedPanel();
     initlayout();
     
     
   
        
   })