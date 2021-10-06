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
import models.Lesson;
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

@WebServlet(name = "SolutionServlet", urlPatterns = {"/solution"})
public class SolutionServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        EvolutionManager evolutionManager = ServletUtils.getEvolutionManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        int evolutionId = ServletUtils.getIntParameter(request, Constants.EVOLUTION_SETTINGS_ID);
        EvolutionProblem evolutionProblem;
        evolutionProblem = evolutionManager.getEvolutionProblemsMap().get(evolutionId);

        int objectId = ServletUtils.getIntParameter(request, "objectId");
        String viewType = request.getParameter("type");

        // log and create the response json string
        SolutionServlet.showSolution cav = new SolutionServlet.showSolution(0,evolutionProblem,username, viewType, objectId);
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

    private static class showSolution {
        private String solutionFitness;
        private Boolean viewingOptions;
        private String RawSolution;
        private boolean isValidTable;

        public showSolution( int version, EvolutionProblem evolutionProblem,String username, String type, int id) {

            RunEvolutionaryTask task = evolutionProblem.getEvolutionRuns().get(username);
            viewingOptions = true;
            SolutionFitness<Lesson> solution = task.getGlobalSolution();
            int totalDays=evolutionProblem.getTimeTable().getTimeTableMembers().getDays();
            int totalHours=evolutionProblem.getTimeTable().getTimeTableMembers().getHours();
            Set<Integer> teacherIds =evolutionProblem.getTimeTable().getTimeTableMembers().getTeachers().keySet();
            Set<Integer> classIds =evolutionProblem.getTimeTable().getTimeTableMembers().getGrades().keySet();
            TimeTableMembers solTimeTableDetails=evolutionProblem.getTimeTable().getTimeTableMembers();
            if(type.equals("raw"))
                showRawSolution(solution, evolutionProblem.getTimeTable());
            else if(type.equals("class"))
                showTable("Class",id,solution.getSolution(),totalDays,totalHours,solution,solTimeTableDetails);
            else if(type.equals("teacher")){
                showTable("Teacher",id,solution.getSolution(),totalDays,totalHours,solution,solTimeTableDetails);
            }
        }

        private void setTeacherIdsMenu(Set<Integer> teacherIds){
            StringBuilder sb = new StringBuilder();
            sb.append("<form action=\"/action_page.php\">");
            sb.append("<label for=\"\">Choose an id:</label>");
            sb.append("<select name=\"id\" id=\"TeacherId\">");
            for (int id:teacherIds) {
                sb.append(String.format("<option value=\"%d\">%d</option>", id, id));
            }
            sb.append("</select>");
            sb.append("<br><br>");
            sb.append("<input type=\"submit\" value=\"Submit\">");
            sb.append("</form>");
           // this.teacherIdsMenu = sb.toString();
        }

        public void showRawSolution(SolutionFitness<Lesson> solutionF, TimeTableDataSet dataSet){
            Solution<Lesson> timeTableSolution = dataSet.sort(solutionF.getSolution(), LessonSortType.DayTimeOriented.toString(),null);
            StringBuilder sb = new StringBuilder();
            //    createHTMLScrollBar(sb);
            for (int i = 0; i < timeTableSolution.getList().size(); i++) {
                int classId = timeTableSolution.getList().get(i).getClassId();
                int teacher = timeTableSolution.getList().get(i).getTeacherId();
                int subject = timeTableSolution.getList().get(i).getSubjectId();
                int day = timeTableSolution.getList().get(i).getDay();
                int hour = timeTableSolution.getList().get(i).getHour();
                if (teacher != -1 && subject != -1) {
                    sb.append(String.format("<p>Day: %d, hour: %d, classID: %d, teacherID: %d, subject: %d</p>", day, hour, classId, teacher, subject));
                }
            }
            sb.append("</body>");
            this.RawSolution = sb.toString();
        }


        public void showTable(String objectType, int objectId, Solution bestSolution, int totalDays, int totalHours, SolutionFitness globalSolution, TimeTableMembers solMembersDetails){
            HashMap<Integer, List<String>> lessonsToAdd = new HashMap<>();
            if(objectType.equals("Teacher")){
                lessonsToAdd= getLessonsContent("Class", objectId, bestSolution, totalDays, totalHours, solMembersDetails);
            }
            else if (objectType.equals("Class")){
                lessonsToAdd= getLessonsContent("Teacher", objectId, bestSolution, totalDays, totalHours, solMembersDetails);
            }
            buildTable(objectType, objectId,lessonsToAdd,totalDays,totalHours,globalSolution );
        }


        private HashMap<Integer, List<String>> getLessonsContent(String objectType, int typeTableId, Solution <Lesson> allLessons, int totalDays, int totalHours, TimeTableMembers solMembersDetails){
            isValidTable=true;
            String lessonsContent="";
            String lesson;
            Solution <Lesson> lessonsPerObject;
            if(objectType.equals("Class")){
                lessonsPerObject= getTeacherSolution(allLessons, typeTableId);
            }
            else{
                lessonsPerObject= getClassSolution(allLessons, typeTableId);
            }
            List<Lesson> dayHourSolution= new ArrayList<Lesson>();
            List<String> lessonsData = new ArrayList<>();
            HashMap<Integer, List<String>> lessonsPerDay = new HashMap<>();
            for(int day=1; day<=totalDays; day++) {
                List<String> allHoursLessonsInADay = new ArrayList<String>();
                for (int hour = 1; hour <= totalHours; hour++) {
                    lesson="";
                    dayHourSolution = getDayHourSolution(lessonsPerObject, day, hour);
                    if(dayHourSolution.size()==0){
                        lessonsContent =" ";
                    }
                    else if (dayHourSolution.size() > 1) isValidTable = false;
                    for (int i = 0; i < dayHourSolution.size(); i++) {
                        if (objectType.equals("Teacher")) {
                            lesson = String.format("%s %d %s, Subject %d %s", objectType, dayHourSolution.get(i).getTeacherId(),solMembersDetails.getTeachers().get(dayHourSolution.get(i).getTeacherId()).getName(), dayHourSolution.get(i).getSubjectId(),solMembersDetails.getSubjects().get(dayHourSolution.get(i).getSubjectId()).getName());

                        } else if (objectType.equals("Class")) {
                            lesson = String.format("%s %d %s, Subject %d %s ", objectType, dayHourSolution.get(i).getClassId(),solMembersDetails.getGrades().get(dayHourSolution.get(i).getClassId()).getName(), dayHourSolution.get(i).getSubjectId(),solMembersDetails.getSubjects().get(dayHourSolution.get(i).getSubjectId()).getName());

                        }
                        if(i>=1) lessonsContent+="<br>";
                        lessonsContent += lesson;
                    }
                    allHoursLessonsInADay.add(lessonsContent);

                    lessonsData.add(lessonsContent);
                    lessonsContent = "";
                }
                lessonsPerDay.put(day, allHoursLessonsInADay);
            }
            return lessonsPerDay;
        }


        public List<Lesson> getDayHourSolution(Solution<Lesson> solution,int day, int hour){
            Solution<Lesson> solutionPerTime= new Solution<Lesson>();
            for(int i=0; i<solution.getList().size(); i++){
                if((solution.getList().get(i).getDay()==day)&&(solution.getList().get(i).getHour()==hour)){
                    if(solution.getList().get(i).getTeacherId()!=-1&&solution.getList().get(i).getSubjectId()!=-1)
                        solutionPerTime.getList().add(solution.getList().get(i));
                }
            }
            return solutionPerTime.getList();
        }

        private Solution<Lesson> getClassSolution(Solution<Lesson> timeTableSolution, int classId){
            Solution<Lesson> solutionPerClass= new Solution<Lesson>();
            for(int i=0; i<timeTableSolution.getList().size(); i++){
                if(timeTableSolution.getList().get(i).getClassId()==classId){
                    solutionPerClass.getList().add(timeTableSolution.getList().get(i));
                }
            }
            return solutionPerClass;
        }

        public Solution <Lesson> getTeacherSolution(Solution<Lesson> timeTableSolution, int teacherId){
            Solution<Lesson> solutionPerTeacher= new Solution<Lesson>();
            for(int i=0; i<timeTableSolution.getList().size(); i++){
                if(timeTableSolution.getList().get(i).getTeacherId()==teacherId){
                    solutionPerTeacher.getList().add(timeTableSolution.getList().get(i));
                }
            }
            return solutionPerTeacher;
        }

        private void createDaysInTable(int totalDays, StringBuilder page){
            page.append("<tr>");
            page.append("<th>   </th>");
            for(int i =1; i<=totalDays;i++){
                page.append(String.format("<th>Day: %d</th>",i));
            }
            page.append("</tr>");
        }

        private void showRulesDetails(HashMap<IRule, Double> rulesFitness, StringBuilder page){
            page.append("<h2>Rules Details:</h2>");
            String ruleTypeLabel;
            for (Map.Entry<IRule, Double> entry : rulesFitness.entrySet()){
                String ruleNameLabel = String.format("<h5><u>Rule name: %s</u></h5>", entry.getKey().getName());
                if(entry.getKey().isHard()){
                    ruleTypeLabel = "<p>Rule type: hard</p>";
                }
                else{
                    ruleTypeLabel = "<p>Rule type: soft</p>";
                }
                String ruleGrade = String.format("<p>Rule grade: %,.1f</p>", entry.getValue());
                page.append(ruleNameLabel);
                page.append(ruleTypeLabel);
                page.append(ruleGrade);
            }
        }
        private void buildTable(String typeTitle, int typeIdTitle,HashMap<Integer,List<String>> tableContentLst , int totalDays, int totalHours, SolutionFitness solutionDetails) {
            StringBuilder page = new StringBuilder();
            page.append("<style>");
            page.append("table, th, td {\n" +
                    "  border:1px solid black;\n" +
                    "}");
            page.append("</style>");
            page.append("<body>");
            page.append("<h2>"+ typeTitle+ " ID: " + typeIdTitle+ "</h2>");
            page.append("<table style=\"width:100%\">");
            createDaysInTable(totalDays, page);

            for(int hour=0; hour<totalHours; hour++){
                page.append("<tr>");
                page.append(String.format("<th>Hour: %d</th>", hour+1));
                for(int day=1; day<=totalDays; day++){
                    List <String> lessonsInADay= tableContentLst.get(day);
                    page.append("<th>"+ lessonsInADay.get(hour)+"</th>");
                }
                page.append("/<tr>");
            }
            page.append("</table>");
            String validword= isValidTable? "":"not";
            String validMsg= String.format("This table is %s valid<br>",validword);
            page.append("<p>"+validMsg+"</p>");
            showRulesDetails(solutionDetails.getRulesFitness(),page);
            page.append("</body>");
            this.solutionFitness = page.toString();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
