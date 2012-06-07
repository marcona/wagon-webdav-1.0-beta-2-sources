package org.apache.maven.wagon.providers.webdav;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
/**
 * Hack AGI
 */
public class AgiHttpUtil {
    public static void customizeHttpClient(HttpClient httpClient) throws Exception {
        // Configure proxySettings if it is required
        //See http://jira.codehaus.org/browse/WAGON-82

//        String login = "agif";
//        String password = "agif";
//        String proxyUser = "GROUPE\\MARCONA";
//        String proxyPassword = "XXXX";
//        String proxyHost = "ehttp1";
//        int proxyPort = 80;

        NewHttpServerCredential newHttpServerCredential = buildCredentialsFromSettings();

        String login = newHttpServerCredential.getUserName();
        String password = newHttpServerCredential.getPassword();
        String proxyUser = newHttpServerCredential.getProxyUserName();
        String proxyPassword = newHttpServerCredential.getProxyPassword();
        String proxyHost = newHttpServerCredential.getProxyHost();
        int proxyPort = newHttpServerCredential.getProxyPort();

        httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
        httpClient.getState().setProxyCredentials(null, proxyHost, new UsernamePasswordCredentials(proxyUser,
                                                                                                   proxyPassword));
        httpClient.getState().setAuthenticationPreemptive(true);
        httpClient.getState().setCredentials(null, new UsernamePasswordCredentials(login, password));
    }


    public static void main(String[] args) throws Exception {
        HttpClient httpClient = new HttpClient();

        AgiHttpUtil.customizeHttpClient(httpClient);

        String path = "http://marconnet.homedns.org/webdav/toto";
        try {
            int exist = httpClient.executeMethod(new GetMethod(URIUtil.encodePath(path)));
            System.out.println("exist = " + exist);
        }
        catch (IOException e) {
            e.printStackTrace();  // Todo
        }
    }


    static NewHttpServerCredential buildCredentialsFromSettings() throws Exception {
        NewHttpServerCredential credential = new NewHttpServerCredential();

        String settingsFilePath = System.getProperty("user.home");
        Xpp3Dom dom = Xpp3DomBuilder.build(new FileReader(settingsFilePath + "/.m2/settings.xml"));
        fillServerCredentialsFromMavenSettings(dom, credential, "proxies", "proxyAllianz", "proxy");
        fillServerCredentialsFromMavenSettings(dom, credential, "servers", "codjo-binary-repository", "server");
        return credential;
    }


    static void fillServerCredentialsFromMavenSettings(Xpp3Dom dom, NewHttpServerCredential credential,
                                                       String nodListIdentifier,
                                                       String nodeIdToFind,
                                                       String nodeItemIdentifier) {
        Xpp3Dom[] proxyChild = dom.getChild(nodListIdentifier).getChildren(nodeItemIdentifier);
        for (int i = 0; i < proxyChild.length; i++) {
            Xpp3Dom proxyNode = proxyChild[i];
            if (nodeIdToFind.equals(proxyNode.getChild("id").getValue())) {
                if ("proxy".equals(nodeItemIdentifier)) {
                    credential.setProxyUserName(proxyNode.getChild("username").getValue());
                    credential.setProxyPassword(proxyNode.getChild("password").getValue());
                    credential.setProxyHost(proxyNode.getChild("host").getValue());
                    credential.setProxyPort(Integer.parseInt(proxyNode.getChild("port").getValue()));
                }
                else {
                    credential.setUserName(proxyNode.getChild("username").getValue());
                    credential.setPassword(proxyNode.getChild("password").getValue());
                }
            }
        }
    }


    static class NewHttpServerCredential {
        private String userName;
        private String password;
        private String proxyUserName;
        private String proxyPassword;
        private String proxyHost;
        private int proxyPort;


        public String toString() {
            return "NewHttpServerCredential{" +
                   "userName='" + userName + '\'' +
                   ", password='" + password + '\'' +
                   ", proxyUserName='" + proxyUserName + '\'' +
                   ", proxyPassword='" + proxyPassword + '\'' +
                   ", proxyHost='" + proxyHost + '\'' +
                   ", proxyPort=" + proxyPort +
                   '}';
        }


        public String getPassword() {
            return password;
        }


        public String getUserName() {
            return userName;
        }


        public void setUserName(String userName) {
            this.userName = userName;
        }


        public String getProxyUserName() {
            return proxyUserName;
        }


        public void setPassword(String password) {
            this.password = password;
        }


        public void setProxyUserName(String proxyUserName) {
            this.proxyUserName = proxyUserName;
        }


        public String getProxyPassword() {
            return proxyPassword;
        }


        public void setProxyPassword(String proxyPassword) {
            this.proxyPassword = proxyPassword;
        }


        public String getProxyHost() {
            return proxyHost;
        }


        public void setProxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
        }


        public int getProxyPort() {
            return proxyPort;
        }


        public void setProxyPort(int proxyPort) {
            this.proxyPort = proxyPort;
        }
    }
}
