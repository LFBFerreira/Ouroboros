package luisf.ouroboros.timemachine;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.properties.PropertyManager;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class TimeMachine {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private URL repoUrl;
    private File checkoutfolder;

    private int numCheckouts = 0;
    private String branchName;

    // ================================================================

    public TimeMachine(URL repoUrl, File checkoutFolder, int numCheckouts) {
        this.repoUrl = repoUrl;
        this.checkoutfolder = checkoutFolder;
        this.numCheckouts = numCheckouts;

        loadProperties();
    }

    private void loadProperties() {
        branchName = props.getString("code.branchName");
    }

    public List<File> checkout() {

        List<File> folderList = new LinkedList<>();

        Boolean isFolderReady = prepareCheckoutFolder(checkoutfolder);

        // create File from Path
        File tempFolder = createTempDirectory("ouroboros");

        Git git = null;

        // clone repository
        try {
            log.warning(Handy.f("Cloning %s - %s", branchName, repoUrl));
            log.warning(Handy.f("It might take a few seconds"));

            git = Git.cloneRepository()
                    .setURI(repoUrl + ".git")
                    .setDirectory(tempFolder)
                    .setBranchesToClone(Arrays.asList(branchName))
                    .call();

        } catch (GitAPIException e) {
            log.severe("Couldn't clone the repository");
            e.printStackTrace();
        }

        // initialize repo
        Repository repository = git.getRepository();

        List<String> commitsToCheckout = getCommitIDs(git, repository, numCheckouts);

        // checkout specific ID
        try {
            for (String id : commitsToCheckout) {
                log.info("Checking out commit " + id);

                git.checkout()
                        .setCreateBranch(true)
                        .setName("commit_" + id)
                        .setStartPoint(id)
                        .call();

                File destinationFolder =new File(checkoutfolder, id);
                copyFolder(tempFolder, destinationFolder);
                folderList.add(destinationFolder);
            }
        } catch (GitAPIException e) {
            log.severe("Couldn't checkout the commit");
            e.printStackTrace();
        }

        git.close();

        return folderList;
    }

    private List<String> getCommitIDs(Git git, Repository repository, int numCheckouts) {
        List<String> commitsToCheckout = new LinkedList<>();

        // initialize commit iterator
        Iterable<RevCommit> commitIterator = null;
        try {
            commitIterator = git.log().add(repository.resolve(branchName)).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return commitsToCheckout;
        } catch (IOException e) {
            e.printStackTrace();
            return commitsToCheckout;
        }

        List<String> commitNames = generateCommitsList(commitIterator);

        for(int i=0 ; i < commitNames.size() ; i += commitNames.size()/numCheckouts)
        {
            commitsToCheckout.add(commitNames.get(i));
        }

        return commitsToCheckout;
    }

    // ================================================================

    // Helpers

    private List<String> generateCommitsList(Iterable<RevCommit> commitIterator)
    {
        List<String> commitNames = new LinkedList<>();

        for (RevCommit commit : commitIterator) {
            PersonIdent authorIdent = commit.getAuthorIdent();
            commitNames.add(commit.getName());
        }

        return commitNames;
    }

    private File createTempDirectory(String prefix)
    {
        Path checkoutPath = null;

        try {
            checkoutPath = Files.createTempDirectory(prefix);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return checkoutPath.toFile();
    }


    private Boolean prepareCheckoutFolder(File checkoutFolder) {
        if (checkoutFolder == null) {
            log.severe("The models folder is null. Cannot checkout");

            return false;
        }

        // check if folder exists
        if (!checkoutFolder.exists()) {
            try {
                log.severe(Handy.f("The selected checkout folder doesn't exist '%s'", checkoutFolder.getCanonicalPath()));
            } catch (IOException e) {
                log.severe("An exception occurred while getting the canonical path");
                e.printStackTrace();
                return false;
            }
        }

        // check if folder is empty
        if (checkoutFolder.isDirectory() && checkoutFolder.listFiles().length > 0) {
            try {
                FileUtils.cleanDirectory(checkoutFolder);
            } catch (IOException e) {
                log.severe("Couldn't clean the folder");
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }


    private boolean copyFolder(File source, File destination) {
        if (!destination.exists()) {
            Handy.createFolder(destination);
        }

        try {
            FileUtils.copyDirectory(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
