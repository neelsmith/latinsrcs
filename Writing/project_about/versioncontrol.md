#Guide to Github

All of the work on this project was done using Git, a distributed version control system.  A version control system keeps track of all the changes made to a file or a set of files so that specific versions can be retrieved.  A distributed version control system, such as Git, lets multiple computers "check out" the files under version control, which are called a repository.  When the repository is checked out on multiple servers, it is backed up every time - so if one computer crashes, all the data is not lost.  In addition, all the changes are tracked, and when the repository is pushed back up to the main server, in our case hosted by Github, the changes made on different computers are reconciled and merged together.  This allows for collaborative work without risk of losing any vital information, since all versions of the repository are stored and can be accessed.

Any kind of file can be used in a version control system, but it works best with markup language and plain text, since they are not dependent on any single kind of environment.  This is especially beneficial for collaborative work, because it means I can do my work on a PC, on a Mac, even on a Linux machine, and everything is easily pushed up into the repository and merged.

There are many options for version control systems, but this project uses Git.  Unlike other systems, Git has a web interface called [Github][github], where the information in the repository can be edited online, further opening up the possibilities for collaboration.  With Github, I do not even need an XML editing software to edit my XML editions, because I can do it directly in the repository.  That being said, Github does not validate XML, so I prefer to use an editing software - but for editing on the fly, it is not necessary.  Github also lets users who are not members of the repository "fork" the information, that is, make a copy of a repository and make local changes to it.  If the user makes changes that he thinks would benefit the whole repository, he can send a "pull request" to the owner of the repository, asking to merge his changes with the original material.  This is a powerful tool for collaboration on things like coding projects and web development, and equally powerful for digital scholarly editing.

[github]: http://github.com

The Git repository for this project includes the digital diplomatic editions, the groovy scripts that run automated analysis or search through the material, and all the data that makes up the RDF graph, stored in .csv tables.  Browse our Github [here][latinsrcs].

[latinsrcs]: http://github.com/neelsmith/latinsrcs

###Notes

- talk about sevlet