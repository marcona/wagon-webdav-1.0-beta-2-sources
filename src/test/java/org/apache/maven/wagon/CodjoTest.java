package org.apache.maven.wagon;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.codehaus.plexus.util.FileUtils;
/**
 *
 */
public class CodjoTest extends WagonTestCase {
    private boolean insideAllianz = false;


    protected AuthenticationInfo getAuthInfo() {
        AuthenticationInfo authenticationInfo = new AuthenticationInfo();

        if (insideAllianz) {
            authenticationInfo.setUserName("tomcat");
            authenticationInfo.setPassword("tomcat");
        }
        else {
            authenticationInfo.setUserName("codjo");
            authenticationInfo.setPassword("$amsung666");
        }
        return authenticationInfo;
    }


    @Override
    protected String getTestRepositoryUrl() throws IOException {
        if (insideAllianz) {
            return "dav:http://wp-sic";
//            return "dav:http://wp-sic:8080/bud/repository/";
        }
        else {
            return "dav:http://repo.codjo.net/maven2/inhouse";
        }
    }


    @Override
    public void testWagonGetFileList() throws Exception {
        setupRepositories();

        setupWagonTestingFixtures();

        String dirName = "maven2/net";

        String filenames[] =
              new String[]{"test-resource.txt", "test-resource-b.txt", "test-resource.pom", "more-resources.dat"};

        for (int i = 0; i < filenames.length; i++) {
            putFile(dirName + "/" + filenames[i], dirName + "/" + filenames[i], filenames[i] + "\n");
        }

        Wagon wagon = getWagon();

        wagon.connect(testRepository, getAuthInfo(), getProxyInfo());

        List list = wagon.getFileList(dirName);
        assertNotNull("file list should not be null.", list);
        assertTrue("file list should contain 4 or more items (actually contains " + list.size() + " elements).", list
                                                                                                                       .size()
                                                                                                                 >= 4);

        for (int i = 0; i < filenames.length; i++) {
            assertTrue("Filename '" + filenames[i] + "' should be in list.", list.contains(filenames[i]));
        }

        wagon.disconnect();

        tearDownWagonTestingFixtures();
    }


    @Override
    public void testWagonPutDirectory() throws Exception {
        setupRepositories();

        setupWagonTestingFixtures();

        Wagon wagon = getWagon();


        if (wagon.supportsDirectoryCopy()) {
            sourceFile = new File(FileTestUtils.getTestOutputDir(), "directory-copy");

            FileUtils.deleteDirectory(sourceFile);

            writeTestFile("test-resource-1.txt");
            writeTestFile("a/test-resource-2.txt");
            writeTestFile("a/b/test-resource-3.txt");
            writeTestFile("c/test-resource-4.txt");
            writeTestFile("d/e/f/test-resource-5.txt");

            wagon.connect(testRepository, getAuthInfo(), getProxyInfo());

            destFile = FileTestUtils.createUniqueFile(getName(), getName());
            //Test de la connexion
            wagon.get("net/codjo/pyp/codjo-pyp/1.5/codjo-pyp-1.5.pom", destFile);

            wagon.putDirectory(sourceFile, "/maven2/net/directory-copy");

            destFile.deleteOnExit();

            wagon.get("/maven2/net/directory-copy/test-resource-1.txt", destFile);
            wagon.get("/maven2/net/directory-copy/a/test-resource-2.txt", destFile);
            wagon.get("/maven2/net/directory-copy/a/b/test-resource-3.txt", destFile);
            wagon.get("/maven2/net/directory-copy/c/test-resource-4.txt", destFile);
            wagon.get("/maven2/net/directory-copy/d/e/f/test-resource-5.txt", destFile);

            wagon.disconnect();
        }

        tearDownWagonTestingFixtures();
    }


    private ProxyInfo getProxyInfo() {
        ProxyInfo proxyInfo = new ProxyInfo();
        proxyInfo.setHost("ehttp1");
//        proxyInfo.setNtlmHost("ehttp1");
//        proxyInfo.setNtlmDomain("GROUPE");
        proxyInfo.setPort(80);
        proxyInfo.setUserName("GROUPE\\MARCONA");
        proxyInfo.setPassword("ELIOTTA7");
        proxyInfo.setNonProxyHosts("wp-sic");
        proxyInfo.setType("HTTP");
        return proxyInfo;
    }


    @Override
    public void testWagonGetFileListWhenDirectoryDoesNotExist() throws Exception {
        ;
    }


    @Override
    public void testWagonResourceExists() throws Exception {
        ;
    }


    @Override
    public void testWagonResourceNotExists() throws Exception {
        ;
    }


    @Override
    protected String getProtocol() {
        return "dav";
    }


    @Override
    public void testWagon() throws Exception {
        ;
    }

    @Override
    public void testWagonPutDirectoryDeepDestination() throws Exception {
        ;
    }


    @Override
    public void testWagonPutDirectoryWhenDirectoryAlreadyExists() throws Exception {
        ;
    }


    @Override
    public void testFailedGet() throws Exception {
        ;
    }
}
