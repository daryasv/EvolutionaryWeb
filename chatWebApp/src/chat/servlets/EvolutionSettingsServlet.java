package chat.servlets;

//taken from: http://www.servletworld.com/servlet-tutorials/servlet3/multipartconfig-file-upload-example.html
// and http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

import chat.constants.Constants;
import chat.models.EvolutionProblemItem;
import chat.models.EvolutionProblems;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import chatEngine.evolution.EvolutionManager;
import com.google.gson.Gson;
import exception.ValidationException;
import models.TimeTableDataSet;
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

@WebServlet(name = "EvolutionSettingsServlet", urlPatterns = {"/get_settings","/pages/chatroom/upload_settings"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class EvolutionSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        List<EvolutionProblemItem> items = new ArrayList<>();
        int clientVersion = ServletUtils.getIntParameter(request, Constants.SETTINGS_VERSION_PARAM);
        if (clientVersion == Constants.INT_PARAMETER_ERROR) {
            return;
        }
        int managerVersion = 0;
        synchronized (getServletContext()) {
            EvolutionManager evolutionManager = ServletUtils.getEvolutionManager(getServletContext());
            managerVersion = evolutionManager.getVersion();
            items = evolutionManager.getEvolutionProblemsMap().values().stream().map(EvolutionProblemItem::new).collect(Collectors.toList());
        }

        EvolutionProblems problems = new EvolutionProblems(managerVersion, items);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(problems);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        Collection<Part> parts = request.getParts();

        StringBuilder fileContent = new StringBuilder();
        try {
            for (Part part : parts) {
                if (!part.getHeader("content-type").equals("text/xml")) {
                    throw new ValidationException("File is not xml");
                }
                //to write the content of the file to a string
                fileContent.append(readFromInputStream(part.getInputStream()));
            }

            TimeTableDataSet timeTableDataSet = loadFileContent(fileContent.toString());
            List<EvolutionProblemItem> items = new ArrayList<>();
            String username = SessionUtils.getUsername(request);
            int settingsVersion = 0;
            synchronized (getServletContext()) {
                EvolutionManager evolutionManager = ServletUtils.getEvolutionManager(getServletContext());
                evolutionManager.addEvolutionProblem("file", username, timeTableDataSet);
                settingsVersion = evolutionManager.getVersion();
                items = evolutionManager.getEvolutionProblemsMap().values().stream().map(EvolutionProblemItem::new).collect(Collectors.toList());
            }
            EvolutionProblems problems = new EvolutionProblems(settingsVersion, items);
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(problems);
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        } catch (ValidationException e) {
            ServletUtils.setError(e.getMessage(),response);
        }
    }

    private TimeTableDataSet loadFileContent(String fileContent) throws ValidationException {
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
            throw new ValidationException("Failed to parse xml. Check if suites the xsd");
        }
    }


    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

}