/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcatche.business.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author jack
 */
public class Register extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
      private static final Logger log = Logger.getLogger(Register.class);
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         String username=request.getParameter("username");
        String password=request.getParameter("password");
        UserLogin ulogin=new UserLogin();
        Map<String,Object> login_obj=ulogin.register(username, password);
        log.debug(login_obj.get("issuccess").toString());
        if(Boolean.parseBoolean(login_obj.get("issuccess").toString())){
            request.getSession().setAttribute("username",username);
            request.getSession().setAttribute("logintime",login_obj.get("logintime"));
            request.getSession().setAttribute("usertype",login_obj.get("usertype"));   
            response.sendRedirect("");
             
        }else{
        request.getSession().setAttribute("registererromsg",login_obj.get("msg"));
         response.sendRedirect("register.jsp");
        }
       
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
