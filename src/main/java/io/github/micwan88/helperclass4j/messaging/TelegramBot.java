package io.github.micwan88.helperclass4j.messaging;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

public class TelegramBot {
	public static final String URL_TELEGRAM_BOT_BASE = "https://api.telegram.org/bot";
	public static final String URL_TELEGRAM_BOT_SENDMSG_CMD = "/sendMessage";
	
	public static final String TELEGRAM_BOT_SENDMSG_PARAM_CHATID = "chat_id";
	public static final String TELEGRAM_BOT_SENDMSG_PARAM_MSGTEXT = "text";
	public static final String TELEGRAM_BOT_SENDMSG_PARAM_PARSE_MODE = "parse_mode";
	public static final int TELEGRAM_BOT_SENDMSG_MAXLENGTH = 4096;
	
	public static final String TELEGRAM_BOT_SENDMSG_VALUE_PARSE_MODE_HTML = "HTML";
	
	public static final String TELEGRAM_BOT_RESPMSG_OK_PATTERN = "^\\{\"ok\"\\s*\\:\\s*true.*$";
	
	private static final Logger myLogger = LogManager.getLogger(TelegramBot.class);
	
	private String tgBotToken = null;
	
	public String getTgBotToken() {
		return tgBotToken;
	}

	public void setTgBotToken(String tgBotToken) {
		this.tgBotToken = tgBotToken;
	}

	public TelegramBot(String tgBotToken) {
		this.tgBotToken = tgBotToken;
	}
	
	public int postNotifications(String notificationMsg, String tgBotChatIDs) {
		Pattern tgBotResponseMsgOkPattern = Pattern.compile(TELEGRAM_BOT_RESPMSG_OK_PATTERN);
		Matcher tgBotResponseMsgOkMatcher = tgBotResponseMsgOkPattern.matcher("");
		
		String apiURL = URL_TELEGRAM_BOT_BASE + tgBotToken + URL_TELEGRAM_BOT_SENDMSG_CMD;
		
		String postMsg = notificationMsg;
		if (postMsg.length() > TELEGRAM_BOT_SENDMSG_MAXLENGTH)
			postMsg = notificationMsg.substring(0, TELEGRAM_BOT_SENDMSG_MAXLENGTH);
		
		myLogger.debug("Start postNotification URL: {}", apiURL);
		myLogger.debug("postMsg: {}", postMsg);
		
		String[] tgBotChatIDArray = tgBotChatIDs.split(",");
		
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			for (String tgBotChatID : tgBotChatIDArray) {
				myLogger.debug("Post to chatID: {}", tgBotChatID);
				
				JsonObject outputJson = new JsonObject();
				outputJson.addProperty(TELEGRAM_BOT_SENDMSG_PARAM_CHATID, tgBotChatID);
				outputJson.addProperty(TELEGRAM_BOT_SENDMSG_PARAM_MSGTEXT, postMsg);
				outputJson.addProperty(TELEGRAM_BOT_SENDMSG_PARAM_PARSE_MODE, TELEGRAM_BOT_SENDMSG_VALUE_PARSE_MODE_HTML);
				myLogger.debug("outputJson: {}", outputJson.toString());
				
				HttpPost httpPost = new HttpPost(apiURL);
				httpPost.setEntity(new StringEntity(outputJson.toString(), "UTF-8"));
				httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
				
				try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
					HttpEntity httpEntity = httpResponse.getEntity();
					if (httpEntity == null) {
						myLogger.debug("postNotification error chatID: {} - No content return", tgBotChatID);
						return -1;
					}
					
					String responseMsg = EntityUtils.toString(httpEntity, "UTF-8");
					if (responseMsg == null) {
						myLogger.debug("postNotification error chatID: {} - No content return", tgBotChatID);
						return -1;
					}
					
					tgBotResponseMsgOkMatcher.reset(responseMsg);
					if (!tgBotResponseMsgOkMatcher.find()) {
						myLogger.debug("postNotification error chatID: {} - responseMsg: {}", tgBotChatID, responseMsg);
						return -1;
					}
				} finally {
					myLogger.debug("End postNotification with chatID: {}", tgBotChatID);
				}
			}
			return 0;
		} catch (IOException e) {
			myLogger.error("Cannot execute http request", e);
		} catch (Exception e) {
			myLogger.error("Unexpected error", e);
		}
		return -2;
	}
	
	public String filterTgRestrictedKeywords(String sourceString) {
		return sourceString.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}
