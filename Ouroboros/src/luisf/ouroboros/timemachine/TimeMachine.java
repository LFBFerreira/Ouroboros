package luisf.ouroboros.timemachine;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.properties.PropertyManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
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

        parsedRepoUrl = removeProtocolFromUrl(repoUrl);

        Repository repo = null;

        try {
            repo = new FileRepository(parsedRepoUrl + ".git");
        } catch (IOException e) {
            log.severe("Couldn't create a local repository");
            return;
        }

        Git git = new Git(repo);

        List<Ref> branches = null;
        try {
            branches = git.branchList().call();
        } catch (GitAPIException e) {
            log.severe("Couldn't get the branch list");
            return;
        }


//        for (Ref branch : branches) {
//            String branchName = branch.getName();
//
//            System.out.println("Commits of branch: " + branchName);
//            System.out.println("-------------------------------------");
//
//            Iterable<RevCommit> commits = git.log().add(repo.resolve(branchName)).call();
//
//            List<RevCommit> commitsList = Lists.newArrayList(commits.iterator());
//
//            for (RevCommit commit : commitsList) {
//                System.out.println(commit.getName());
//                System.out.println(commit.getAuthorIdent().getName());
//                System.out.println(new Date(commit.getCommitTime() * 1000L));
//                System.out.println(commit.getFullMessage());
//            }
//        }

        git.close();

        if (saveFolder == null) {
            log.severe("The models folder is null. Cannot checkout");
            return;
        }

        File checkoutFolder = createCheckoutFolder(saveFolder, "test");

        if (checkoutFolder == null || !checkoutFolder.exists()) {
            try {
                log.severe(Handy.f("The selected checkout folder doesn't exist '%s'", checkoutFolder.getCanonicalPath()));
            } catch (IOException e) {
                log.severe("An exception occurred while getting the canonical path");
                e.printStackTrace();
            }

            return;
        }

//        try {
//            log.info(Handy.f("Cloning '%d' commits from '%s'", numCheckouts, repoUrl));
//
//            Git.cloneRepository()
//                    .setURI(repoUrl.toString())
//                    .setDirectory(checkoutFolder)
//                    .call();
//
//            System.out.println("Completed Cloning");
//        } catch (GitAPIException e) {
//            log.severe("Exception occurred while cloning repo");
//            e.printStackTrace();
//        }
    }

    // ================================================================

    private File createCheckoutFolder(File saveFolder, String test) {
        File newFolder = new File(Paths.get(saveFolder.toString(), test).toString());

        try {
            newFolder.mkdir();
        } catch (SecurityException e) {
            log.severe("There was an exception while creating the checkout directory");
            return null;
        }

        return newFolder;
    }


    private String removeProtocolFromUrl(URL repoUrl) {
        String parsedUrl = repoUrl.toString();
        return parsedUrl.replaceFirst("http[s]*://", "");
    }
}
