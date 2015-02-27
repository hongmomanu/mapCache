/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcatche.business.impl;

import java.util.Map;
import mapcatche.business.dbdao.Dbtiandtu;

/**
 *
 * @author jack
 */
public class UserLogin {
  
    public Map<String,Object> register(String username,String passwd){
    
          //Dbtiandtu tddao = new Dbtiandtu();
        Dbtiandtu tddao = new Dbtiandtu();
          
        return tddao.register(username, passwd);
        
    }
    public Map<String,Object> login(String username,String password){
    Dbtiandtu tddao = new Dbtiandtu();
    return tddao.login(username, password);
        
    }
    
}
