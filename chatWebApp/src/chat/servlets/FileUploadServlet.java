//package chat.servlets;
//
////taken from: http://www.servletworld.com/servlet-tutorials/servlet3/multipartconfig-file-upload-example.html
//// and http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
//
//import chat.utils.EvolutionaryTaskMembers;
//import models.TimeTableDataSet;
//import models.evolution.EvolutionConfig;
//import models.timeTable.Grade;
//import models.timeTable.Rule;
//import models.timeTable.Subject;
//import models.timeTable.Teacher;
//import schema.models.ETTDescriptor;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.MultipartConfig;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.Part;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Unmarshaller;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.io.StringReader;
//import java.util.*;
//
//@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
//public class FileUploadServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.sendRedirect("chatroom/chatroom.html");
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.setContentType("text/html");
//        PrintWriter out = response.getWriter();
//
//        Collection<Part> parts = request.getParts();
//
//        StringBuilder fileContent = new StringBuilder();
//
//        for (Part part : parts) {
//            printPart(part, out);
//
//            //to write the content of the file to an actual file in the system (will be created at c:\samplefile)
//            part.write("samplefile");
//
//            //to write the content of the file to a string
//            fileContent.append(readFromInputStream(part.getInputStream()));
//        }
//        try {
//            EvolutionaryTaskMembers evolutionaryMembers = loadFileContent(fileContent.toString());
//            printTimeTableSettings(evolutionaryMembers,out);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
////        printFileContent(fileContent.toString(), out);
//    }
//
//    private EvolutionaryTaskMembers loadFileContent(String fileContent) throws Exception {
//        try{
//            //load xml file into ETT classes
////            if(!absolutePath.endsWith(".xml")){
////                throw new ValidationException("File not xml");
////            }
//            JAXBContext jaxbContext = JAXBContext.newInstance(ETTDescriptor.class);
//            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//            StringReader stringReader = new StringReader(fileContent);
//            ETTDescriptor descriptor = (ETTDescriptor) jaxbUnmarshaller.unmarshal(stringReader);
//            EvolutionaryTaskMembers evolutionaryMembers = new EvolutionaryTaskMembers();
//            evolutionaryMembers.setTimeTable(new TimeTableDataSet(descriptor));
//            evolutionaryMembers.setEvolutionEngineDataSet(new EvolutionConfig(descriptor.getETTEvolutionEngine()));
//
//            //ProgramManager.systemSetting.IS_FILE_LOADED.status=true;
//            System.out.println("File Loaded Successfully!\n");
//            return evolutionaryMembers;
//        } catch (JAXBException e) {
//            //ProgramManager.systemSetting.IS_FILE_LOADED.status=false;
//            System.out.println("Failed to parse xml");
//        }
//        return null;
//    }
//
//    private void printTimeTableSettings(EvolutionaryTaskMembers evolutionaryMembers, PrintWriter out){
//        StringBuilder sbXmlEttSettings = new StringBuilder();
//
//        HashMap<Integer, Subject> subjects = evolutionaryMembers.getTimeTable().getTimeTableMembers().getSubjects();
//        HashMap<Integer, Teacher> teachers = evolutionaryMembers.getTimeTable().getTimeTableMembers().getTeachers();
//        HashMap<Integer, Grade> grades = evolutionaryMembers.getTimeTable().getTimeTableMembers().getGrades();
//        List<Rule> rules = evolutionaryMembers.getTimeTable().getTimeTableMembers().getRules();
//
//        sbXmlEttSettings.append(toStringSubjects(subjects));
//        sbXmlEttSettings.append("\n");
//        sbXmlEttSettings.append(toStringTeachers(teachers, subjects));
//        sbXmlEttSettings.append("\n");
//        sbXmlEttSettings.append(toStringGrades(grades, subjects));
//        sbXmlEttSettings.append("\n");
//        sbXmlEttSettings.append(toStringRules(rules));
//
//        out.println("<h2>File data:</h2>");
//        out.println("<textarea style=\"width:100%;height:400px\">");
//        out.println(sbXmlEttSettings);
//        out.println("</textarea>");
//    }
//
//    private StringBuilder toStringSubjects(HashMap<Integer, Subject> subjects)
//    {
//        StringBuilder sbSubjects = new StringBuilder();
//        sbSubjects.append("SUBJECTS\n");
//        sbSubjects.append("__________________________________________________________\n");
//        for (Map.Entry<Integer, Subject> entry : subjects.entrySet()) {
//            sbSubjects.append(String.format("ID: %d  |  Name: %s\n", entry.getKey(), entry.getValue().getName()));
//        }
//        sbSubjects.append("__________________________________________________________\n");
//        return sbSubjects;
//    }
//
//    private StringBuilder toStringTeachers(HashMap<Integer, Teacher> teachers,HashMap<Integer, Subject> subjects){
//        StringBuilder sbTeachers = new StringBuilder();
//        sbTeachers.append("TEACHERS\n");
//        sbTeachers.append("__________________________________________________________\n");
//        for(Map.Entry<Integer, Teacher > entry : teachers.entrySet()){
//            sbTeachers.append(String.format("Teacher ID %d\n", entry.getKey()));
//            sbTeachers.append(String.format("Teaching subjects:\n"));
//            for(int i=0; i<entry.getValue().getSubjectsIdsList().size();i++){
//                int subjectID=entry.getValue().getSubjectsIdsList().get(i);
//                sbTeachers.append(String.format("       Subject ID: %d  |  ", subjectID));
//                sbTeachers.append(String.format("Name: %s\n", subjects.get(subjectID).getName()));
//            }
//            sbTeachers.append("\n");
//        }
//        sbTeachers.append("__________________________________________________________\n");
//        sbTeachers.append("\n");
//
//        return sbTeachers;
//    }
//
//    private StringBuilder toStringGrades(HashMap<Integer, Grade> grades,HashMap<Integer, Subject> subjects) {
//        StringBuilder sbGrades = new StringBuilder();
//        sbGrades.append("GRADES\n");
//        sbGrades.append("__________________________________________________________\n");
//        for (Map.Entry<Integer, Grade> entry : grades.entrySet()) {
//            sbGrades.append(String.format("\nGrade ID %d\n", entry.getKey()));
//
//            for (Map.Entry<Integer, Integer> required : entry.getValue().getRequirements().entrySet()) {
//                sbGrades.append(String.format("Subject ID: %d  |  Name: %s  |  ", required.getKey(), subjects.get(required.getKey()).getName()));
//                sbGrades.append(String.format("Required Hours: %d", required.getValue()));
//                sbGrades.append("\n");
//            }
//            sbGrades.append("\n");
//        }
//        sbGrades.append("__________________________________________________________\n");
//        sbGrades.append("\n");
//        return sbGrades;
//    }
//
//    private StringBuilder toStringRules(List<Rule> rules){
//        StringBuilder sbRules = new StringBuilder();
//        sbRules.append("RULES\n");
//        sbRules.append("__________________________________________________________\n");
//        for(int i=0; i<rules.size();i++){
//            if(rules.get(i).isHard())
//                sbRules.append(String.format("Rule Name: %s  |  Type: Hard", rules.get(i).getName()));
//            else{
//                sbRules.append(String.format("Rule Name: %s  |  Type: Soft", rules.get(i).getName()));
//            }
//            sbRules.append("\n");
//        }
//        sbRules.append("__________________________________________________________\n");
//        sbRules.append("\n");
//        return sbRules;
//    }
//
//    private void printPart(Part part, PrintWriter out) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<h2>");
//        for (String header : part.getHeaderNames()) {
//            sb.append(header).append(" : ").append(part.getHeader(header)).append("<br>");
//        }
//        sb.append("</h2>");
//        out.println(sb.toString());
//
//    }
//
//    private String readFromInputStream(InputStream inputStream) {
//        return new Scanner(inputStream).useDelimiter("\\Z").next();
//    }
//
//    private void printFileContent(String content, PrintWriter out) {
//        out.println("<h2>File content:</h2>");
//        out.println("<textarea style=\"width:100%;height:400px\">");
//        out.println(content);
//        out.println("</textarea>");
//    }
//}