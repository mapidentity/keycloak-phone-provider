package cc.coopersoft.keycloak.phone.providers.sender;

import cc.coopersoft.keycloak.phone.providers.exception.MessageSendException;
import cc.coopersoft.keycloak.phone.providers.spi.FullSmsSenderAbstractService;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.logging.Logger;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.Config.Scope;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;

public class BulksmsSmsSenderServiceProvider extends FullSmsSenderAbstractService {

    public static final String CONFIG_API_SERVER = "url";
    public static final String CONFIG_API_USERNAME = "username";
    public static final String CONFIG_API_PASSWORD = "password";
    public static final String CONFIG_FROM = "from";

    private static final Logger logger = Logger.getLogger(BulksmsSmsSenderServiceProvider.class);

    private final String url;
    private final String username;
    private final String password;
    private final String from;

    public class BulksmsMessage {
        String from;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        String to;

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        String body;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        String unicode;

        public String getUnicode() {
            return unicode;
        }

        public void setUnicode(String unicode) {
            this.unicode = unicode;
        }

        public BulksmsMessage(String from, String to, String body) {
            this.from = from;
            this.to = to;
            this.body = body;
            this.unicode = "UNICODE";
        }
    }

    BulksmsSmsSenderServiceProvider(Scope config, String realmDisplay) {
        super(realmDisplay);

        String configUrl = config.get(CONFIG_API_SERVER);
        this.url = configUrl != null ? configUrl : "https://api.bulksms.com/v1/messages";
        this.username = config.get(CONFIG_API_USERNAME);
        this.password = config.get(CONFIG_API_PASSWORD);
        this.from = config.get(CONFIG_FROM);
    }

    @Override
    public void sendMessage(String phoneNumber, String message) throws MessageSendException {
        HttpClient httpclient = HttpClients.createDefault();
        SimpleHttp req = SimpleHttp.doPost(url, httpclient);
        req.json(new BulksmsMessage[] { new BulksmsMessage(this.from, phoneNumber, message) });
        req.authBasic(this.username, this.password);
        try {
            SimpleHttp.Response res = req.asResponse();
            if (res.getStatus() >= 200 || res.getStatus() <= 299) {
                logger.debugv("Sending SMS to {0} with contents: {1}. Server responded with: {2}", phoneNumber, message,
                        res.asString());
            } else {
                logger.debugv("Sending SMS to {0} with contents: {1}}. Server responded with: {2}", phoneNumber, message,
                        res.asString());
                throw new MessageSendException("Bulksms API responded with an error.", new Exception(res.asString()));
            }
        } catch (IOException ex) {
            logger.errorv(ex, "IOException while communicating with SMS service {0}.", url);
            throw new MessageSendException("Error while communicating with Bulksms API.", ex);
        }
    }

    @Override
    public void close() {
    }
}
