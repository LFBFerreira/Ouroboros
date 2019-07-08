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
    private String parsedRepoUrl;
    private File checkoutfolder;
    private String pathFromOrigin;
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
        pathFromOrigin = props.getString("code.pathFromOrigin");
        branchName = props.getString("code.branchName");
    }

    public void checkout() {

        Boolean isFolderReady = prepareCheckoutFolder(checkoutfolder);

        // create File from Path
        File tempFolder = createTempDirectory("ouroboros");

        Git git = null;

        // clone repository
        try {
            log.warning(Handy.f("Cloning '%s' from %s. It might take a few seconds", branchName, repoUrl));

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

                copyFolder(tempFolder, new File(checkoutfolder, id));
            }
        } catch (GitAPIException e) {
            log.severe("Couldn't checkout the commit");
            e.printStackTrace();
        }

        git.close();
    }

    private List<String> getCommitIDs(Git git, Repository repository, int numCheckouts) {
        List<String> commitsToCheckout = new LinkedList<>();

        // initialize commit iterator
        Iterable<RevCommit> commitIterator = null;
        try {
            commitIterator = git.log().add(repository.resolve(branchName)).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        List<String> commitsToCheckout = new LinkedList<>();
        int counter = 0;

        // list all commits
        for (RevCommit commit : commitIterator) {
            PersonIdent authorIdent = commit.getAuthorIdent();
            //log.info(Handy.f("%s - %s [%s]", authorIdent.getWhen(), commit.getShortMessage(), commit.getName()));

            if (counter < numCheckouts) {
                commitsToCheckout.add(commit.getName());
            }
            counter++;
        }

        return commitsToCheckout;
    }

    // ================================================================

    // Helpers

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


    private File createFolder(File folder) {
        try {
            folder.mkdir();
        } catch (SecurityException e) {
            log.severe("There was an exception while creating the directory");
            return null;
        }

        return folder;
    }

    private boolean copyFolder(File source, File destination) {
        if (!destination.exists()) {
            createFolder(destination);
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
