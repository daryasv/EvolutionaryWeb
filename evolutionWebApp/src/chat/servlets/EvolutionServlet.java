package chat.servlets;

import chat.constants.Constants;
import chat.models.UserEvConfig;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import chatEngine.evolution.EvolutionManager;
import chatEngine.evolution.EvolutionProblem;
import chatEngine.tasks.RunEvolutionaryTask;
import com.google.gson.Gson;
import engine.models.IRule;
import engine.models.Solution;
import engine.models.SolutionFitness;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.Lesson;

import models.evolution.EvolutionConfig;

import models.LessonSortType;
import models.TimeTableDataSet;
import models.timeTable.TimeTableMembers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "EvolutionServlet", urlPatterns = {"/settings"})
public class EvolutionServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
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

        EvolutionProblem evolutionProblem;
        synchronized (getServletContext()) {
            evolutionProblem = evolutionManager.getEvolutionProblemsMap().get(evolutionId);
        }

        // log and create the response json string
        EvolutionAndVersion cav = new EvolutionAndVersion(0,evolutionProblem,username);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(cav);
        logServerMessage(jsonResponse);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

    }

    private void logServerMessage(String message){
        System.out.println(message);
    }
    
    private static class EvolutionAndVersion {

        final private int version;
        final private String settings;
        private boolean running;
        private boolean finished;
        private double percentage;
        private UserEvConfig evConfig;
        private boolean paused;
        private Boolean viewingOptions;
        private boolean isValidTable;
        Integer currentGeneration;
        Double bestFitness;

        public EvolutionAndVersion( int version, EvolutionProblem evolutionProblem,String username) {
            this.version = version;
            running = false;
            finished = false;
            viewingOptions = false;
            percentage = 0;
            if(evolutionProblem!=null && evolutionProblem.getTimeTable() !=null) {
                this.settings = evolutionProblem.getTimeTableSettings();
                boolean exists = evolutionProblem.getEvolutionRuns().containsKey(username);
                if(exists){
                    RunEvolutionaryTask task = evolutionProblem.getEvolutionRuns().get(username);
                    this.running = task.isRunning();
                    this.finished = task.isFinished();
                    this.paused = task.isPaused();
                    this.percentage = task.getPercentage();
                    this.evConfig = new UserEvConfig(task.getEvolutionConfig());
                    this.currentGeneration = task.getCurrentGeneration();
                    this.bestFitness = task.getCurrentBestFitness();

                    if(this.finished) {
                        viewingOptions = true;

                        int totalDays=evolutionProblem.getTimeTable().getTimeTableMembers().getDays();
                        int totalHours=evolutionProblem.getTimeTable().getTimeTableMembers().getHours();
                        Set<Integer> teacherIds =evolutionProblem.getTimeTable().getTimeTableMembers().getTeachers().keySet();
                        Set<Integer> classIds =evolutionProblem.getTimeTable().getTimeTableMembers().getGrades().keySet();
                    }
                }else{
                    this.evConfig = new UserEvConfig(evolutionProblem.getTimeTable().getEvolutionConfig());
                }
            }else{
                settings = "";
            }
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
