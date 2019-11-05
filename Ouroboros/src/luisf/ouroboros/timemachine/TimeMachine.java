package luisf.ouroboros.timemachine;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.properties.PropertyManager;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

        // choose which commits to checkout
        List<RevCommit> commitsToCheckout = getCommitIDs(git, repository, numCheckouts);

        // checkout specific commit
        try {
            for (RevCommit commitInfo : commitsToCheckout) {
                log.info("Checking out commit " + commitInfo.getName());

                String commitId = commitInfo.getName();

                // checkout specific commit
                git.checkout()
                        .setCreateBranch(true)
                        .setName("commit_" + commitId)
                        .setStartPoint(commitInfo)
                        .call();

                File destinationFolder = new File(checkoutfolder, commitId);

                // copy the checked-out files to their final destination
                copyFolder(tempFolder, destinationFolder);

                saveCommitMetadata(commitInfo, destinationFolder);

                folderList.add(destinationFolder);
            }
        } catch (GitAPIException e) {
            log.severe("Couldn't checkout the commit");
            e.printStackTrace();
        }

        git.close();

        return folderList;
    }


    // ================================================================

    // Helpers

    private void saveCommitMetadata(RevCommit metadata, File destinationFolder) {
        JSONObject obj = new JSONObject();


        PersonIdent authorIdent = metadata.getAuthorIdent();
//        PersonIdent committerIdent = commit.getCommitterIdent();
        Date authorDate = authorIdent.getWhen();
        TimeZone authorTimeZone = authorIdent.getTimeZone();

        // fill object
        obj.put("id", metadata.getName());
        obj.put("date", metadata.getCommitTime());
        obj.put("shortMessage", metadata.getShortMessage());
        obj.put("fullMessage", metadata.getFullMessage());
        obj.put("date", authorDate);
        obj.put("timezone", authorTimeZone);

        File destinationFile = new File(destinationFolder.getParent(), metadata.getName() + ".json");

        // try-with-resources statement based on post comment below :)
        try (FileWriter file = new FileWriter(destinationFile)) {
            file.write(obj.toJSONString());
            log.info(Handy.f("Commit %s metadata saved to %s", metadata.getName(), destinationFile.getPath()));
        } catch (IOException e) {
            log.severe(Handy.f("It was not possible to save the commits metadata to {}",
                    Paths.get(destinationFolder.getPath(), metadata.getName())));
            e.printStackTrace();
        }
    }

    private List<RevCommit> getCommitIDs(Git git, Repository repository, int numCheckouts) {
        List<RevCommit> commitsToCheckout = new LinkedList<>();

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

        List<RevCommit> commitsInfo = generateCommitsList(commitIterator);

        for (int i = 0; i < commitsInfo.size(); i += commitsInfo.size() / numCheckouts) {
            commitsToCheckout.add(commitsInfo.get(i));
        }

        return commitsToCheckout;
    }

    private List<RevCommit> generateCommitsList(Iterable<RevCommit> commitIterator) {
        List<RevCommit> commitNames = new LinkedList<>();

        for (RevCommit commit : commitIterator) {
            PersonIdent authorIdent = commit.getAuthorIdent();
//            commitNames.add(commit.getName());
            commitNames.add(commit);
        }

        return commitNames;
    }

    private File createTempDirectory(String prefix) {
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
