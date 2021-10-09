package chat.utils;

import chatEngine.chat.ChatManager;
import chatEngine.evolution.EvolutionManager;
import chatEngine.users.UserManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static chat.constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
	private static final String EVOLUTION_MANAGER_ATTRIBUTE_NAME = "evolutionManager";

	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained un-synchronized for performance POV
	 */
	private static final Object userManagerLock = new Object();
	private static final Object chatManagerLock = new Object();
	private static final Object evolutionManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {

		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static ChatManager getChatManager(ServletContext servletContext) {
		synchronized (chatManagerLock) {
			if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
			}
		}
		return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
	}

	public static EvolutionManager getEvolutionManager(ServletContext servletContext) {
		synchronized (evolutionManagerLock) {
			if (servletContext.getAttribute(EVOLUTION_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(EVOLUTION_MANAGER_ATTRIBUTE_NAME, new EvolutionManager());
			}
		}
		return (EvolutionManager) servletContext.getAttribute(EVOLUTION_MANAGER_ATTRIBUTE_NAME);
	}

	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}


	public static boolean getBooleanParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			return Boolean.parseBoolean(value);
		}
		return false;
	}

	public static void setError(String text, HttpServletResponse res) {
		res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		res.setContentType("text/plain");
		try {
			res.getWriter().println(text);
			res.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
