jgit-codecommit
===============

Example showing how to clone an [AWS
CodeCommit](<https://aws.amazon.com/codecommit/>) repository over HTTPS using
[jgit](<https://eclipse.org/jgit/>).

AWS CodeCommit requires a new set of credentials every \~15 minutes. The
credentials are generated from an AWS secret access key.

This example shows how to generate the password and present the credentials to
jgit so that the CodeCommit repository can be cloned.

This is the beginning of adding CodeCommit support to [Git for
JIRA](<https://marketplace.atlassian.com/plugins/com.xiplink.jira.git.jira_git_plugin>).
