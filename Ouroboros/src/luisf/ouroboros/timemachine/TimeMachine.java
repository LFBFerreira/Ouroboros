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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class TimeMachine {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private URL repoUrl;
    private String parsedRepoUrl;
    private File saveFolder;

    // ================================================================

    public TimeMachine(URL repoUrl, File saveFolder) {
        this.repoUrl = repoUrl;
        this.saveFolder = saveFolder;
    }


    public void checkout(int numCheckouts) {

        if (saveFolder == null) {
            log.severe("The models folder is null. Cannot checkout");
            return;
        }

        File checkoutFolder = createFolder(new File(saveFolder, "test"));

        if (checkoutFolder == null || !checkoutFolder.exists()) {
            try {
                log.severe(Handy.f("The selected checkout folder doesn't exist '%s'", checkoutFolder.getCanonicalPath()));
            } catch (IOException e) {
                log.severe("An exception occurred while getting the canonical path");
                e.printStackTrace();
            }

            return;
        }

        if (checkoutFolder.isDirectory() && checkoutFolder.listFiles().length > 0) {
            try {
                FileUtils.cleanDirectory(checkoutFolder);
            } catch (IOException e) {
                log.severe("Couldn't clean the folder");
                e.printStackTrace();
            }
        }

        //parsedRepoUrl = removeProtocolFromUrl(repoUrl);


        String branchName = "refs/heads/master"; // tag or branch

        Git git = null;

        // clone repository
        try {
            git = Git.cloneRepository()
                    .setURI(repoUrl + ".git")
                    .setDirectory(checkoutFolder)
                    .setBranchesToClone(Arrays.asList(branchName))
                    .call();
        } catch (GitAPIException e) {
            log.severe("Couldn't clone the repository");
            e.printStackTrace();
        }

        // ---------------------------------------------------------------------------------------------
        // initialize repo

        Repository repository = git.getRepository();

//        File dir = File.createTempFile("gitinit", ".test");
//        if(!dir.delete()) {
//            throw new IOException("Could not delete file " + dir);
//        }


        // initialize commit iterator
        Iterable<RevCommit> commitIterator = null;
        try {
            commitIterator = git.log().add(repository.resolve(branchName)).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> commitsToCheckout = new LinkedList<>();
        int counter = 0;

        // list all commits
        for (RevCommit commit : commitIterator) {
            PersonIdent authorIdent = commit.getAuthorIdent();
            log.info(Handy.f("%s - %s [%s]", authorIdent.getWhen(), commit.getShortMessage(), commit.getName()));

            if (counter < numCheckouts) {
                commitsToCheckout.add(commit.getName());
            }
            counter++;
        }


        // ---------------------------------------------------------------------------------------------

        // checkout specific ID
        try {
            for (String id : commitsToCheckout) {
                git.checkout()
                        .setCreateBranch(true)
                        .setName("commit_" + id)
                        .setStartPoint(id)
                        .call();
            }
        } catch (GitAPIException e) {
            log.severe("Couldn't checkout the commit");
            e.printStackTrace();
        }

        // list branches
//        List<Ref> call = null;
//        try {
//            call = git.branchList().call();
//        } catch (GitAPIException e) {
//            log.severe("Couldn't get the branch list");
//            e.printStackTrace();
//        }
//
//        for (Ref ref : call) {
//            System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
//        }

        git.close();

    }

    // ================================================================

    private File createFolder(File folder) {
        try {
            folder.mkdir();
        } catch (SecurityException e) {
            log.severe("There was an exception while creating the checkout directory");
            return null;
        }

        return folder;
    }
}
