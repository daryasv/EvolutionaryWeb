package chat.servlets;

//taken from: http://www.servletworld.com/servlet-tutorials/servlet3/multipartconfig-file-upload-example.html
// and http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

import chat.models.EvolutionProblemItem;
import chat.models.EvolutionProblems;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import chatEngine.evolution.EvolutionManager;
import chatEngine.evolution.EvolutionaryTaskMembers;
import com.google.gson.Gson;
import models.TimeTableDataSet;
import models.timeTable.Grade;
import models.timeTable.Rule;
import models.timeTable.Subject;
import models.timeTable.Teacher;
import schema.models.ETTDescriptor;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "GetFileUploadServlet", urlPatterns = {"/pages/chatroom/upload_file"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("chatroom/chatroom2.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        Collection<Part> parts = request.getParts();

        StringBuilder fileContent = new StringBuilder();

        for (Part part : parts) {
            if(!part.getHeader("content-type").equals("text/xml")){
                //todo: error;
                System.out.println("Not xml");
            }
            //to write the content of the file to a string
            fileContent.append(readFromInputStream(part.getInputStream()));
        }
        try {
            TimeTableDataSet timeTableDataSet = loadFileContent(fileContent.toString());
            List<EvolutionProblemItem> items = new ArrayList<>();
            if(timeTableDataSet!=null){
                String username = SessionUtils.getUsername(request);
                synchronized (getServletContext()){
                    ServletUtils.getEvolutionManager(getServletContext()).addEvolutionProblem("file",username,timeTableDataSet);
                    items = ServletUtils.getEvolutionManager(getServletContext()).getEvolutionProblemsMap().values().stream().map(EvolutionProblemItem::new).collect(Collectors.toList());
                }
            }
            EvolutionProblems problems = new EvolutionProblems(0,items);
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(problems);
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private TimeTableDataSet loadFileContent(String fileContent) throws Exception {
        try{
            //load xml file into ETT classes
//            if(!absolutePath.endsWith(".xml")){
//                throw new ValidationException("File not xml");
//            }
            JAXBContext jaxbContext = JAXBContext.newInstance(ETTDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            StringReader stringReader = new StringReader(fileContent);
            ETTDescriptor descriptor = (ETTDescriptor) jaxbUnmarshaller.unmarshal(stringReader);
            return new TimeTableDataSet(descriptor);
        } catch (JAXBException e) {
            //ProgramManager.systemSetting.IS_FILE_LOADED.status=false;
            System.out.println("Failed to parse xml");
        }
        return null;
    }


    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private void printFileContent(String content, PrintWriter out) {
        out.println("<h2>File content:</h2>");
        out.println("<textarea style=\"width:100%;height:400px\">");
        out.println(content);
        out.println("</textarea>");
    }
}