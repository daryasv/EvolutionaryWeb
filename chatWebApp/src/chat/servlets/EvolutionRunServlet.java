package chat.servlets;

import chat.constants.Constants;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import chatEngine.evolution.EvolutionManager;
import chatEngine.evolution.EvolutionProblem;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "EvolutionRunServlet", urlPatterns = {"/run_evolution"})
public class EvolutionRunServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EvolutionManager evolutionManager = ServletUtils.getEvolutionManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        /*
        verify chat version given from the user is a valid number. if not it is considered an error and nothing is returned back
        Obviously the UI should be ready for such a case and handle it properly
         */
        int evolutionId = ServletUtils.getIntParameter(request, Constants.EVOLUTION_SETTINGS_ID);
//        if (chatVersion == Constants.INT_PARAMETER_ERROR) {
//            return;
//        }

        /*
        Synchronizing as minimum as I can to fetch only the relevant information from the chat manager and then only processing and sending this information onward
        Note that the synchronization here is on the ServletContext, and the one that also synchronized on it is the chat servlet when adding new chat lines.
         */


        synchronized (getServletContext()) {
            evolutionManager.runEvolution(evolutionId,username);
        }

        try(PrintWriter out = response.getWriter()){
            out.println("success");
            out.flush();
        }
    }

    private void logServerMessage(String message){
        System.out.println(message);
    }

    private static class EvolutionAndVersion {

        final private int version;
        final private String settings;

        public EvolutionAndVersion( int version, EvolutionProblem evolutionProblem) {
            this.version = version;
            this.settings = evolutionProblem.getTimeTableSettings();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
