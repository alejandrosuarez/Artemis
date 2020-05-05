package de.tum.in.www1.artemis.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.URIish;

public class LocalRepository {

    public File localRepoFile;

    public File originRepoFile;

    public Git localGit;

    public Git originGit;

    public void configureRepos(String localRepoFileName, String originRepoFileName) throws Exception {

        this.localRepoFile = Files.createTempDirectory(localRepoFileName).toFile();
        this.localGit = Git.init().setDirectory(localRepoFile).call();

        this.originRepoFile = Files.createTempDirectory(originRepoFileName).toFile();
        this.originGit = Git.init().setDirectory(originRepoFile).call();

        this.localGit.remoteAdd().setName("origin").setUri(new URIish(String.valueOf(this.originRepoFile))).call();
    }

    public void resetLocalRepo() throws IOException {
        if (this.localRepoFile != null && this.localRepoFile.exists()) {
            FileUtils.deleteDirectory(this.localRepoFile);
        }
        if (this.localGit != null) {
            this.localGit.close();
        }

        if (this.originRepoFile != null && this.originRepoFile.exists()) {
            FileUtils.deleteDirectory(this.originRepoFile);
        }
        if (this.originGit != null) {
            this.originGit.close();
        }
    }

    public List<RevCommit> getAllLocalCommits() throws Exception {
        return StreamSupport.stream(this.localGit.log().call().spliterator(), false).collect(Collectors.toList());
    }

    public List<RevCommit> getAllOriginCommits() throws Exception {
        return StreamSupport.stream(this.originGit.log().call().spliterator(), false).collect(Collectors.toList());
    }
}
