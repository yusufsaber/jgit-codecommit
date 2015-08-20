package com.bigbrassband.aws.codecommit.gitclone;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String args[]) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, MalformedURLException, CodeCommitHttpCredentials.CodeCommitHttpCredentialsException, GitAPIException {

        String accessKey = "acces_key_id_goes_here";
        String secret = "secret_access_key_goes_here";

        String httpUrl = "https://git-codecommit.us-east-1.amazonaws.com/v1/repos/githelpertest";

        CodeCommitHttpCredentials codeCommitHttpCredentials = new CodeCommitHttpCredentials(accessKey, secret, httpUrl);


        File localGitRepo = new File("localRepo");
        FileUtils.deleteQuietly(localGitRepo);


        System.out.println(codeCommitHttpCredentials.getUserName());
        System.out.println(codeCommitHttpCredentials.getPassword());


        try (Git result = Git.cloneRepository()
                .setURI(httpUrl)
                .setDirectory(localGitRepo)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                        codeCommitHttpCredentials.getUserName(),
                        codeCommitHttpCredentials.getPassword()))
                .call()) {

            result.getRepository().close();
        }


    }


}
