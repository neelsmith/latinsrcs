# Using GitHub

### Install Git

There are a few options for installing Git.  For basic instructions, see [this article][setup] on GitHub.  There is a Windows-native user interface for Git available [here][windows], but it is just as easy to use the command line.

[setup]: https://help.github.com/articles/set-up-git
[windows]: https://windows.github.com/

One of the Git setup menus asks to adjust your path environment, as seen below.

![Adjusting your PATH environment][screenshot]

[screenshot]: gitsetup.jpg

If you are unfamiliar with working from a command line, choose the first option, "use Git from Git Bash only".  This will allow you to run Git from a separate terminal, rather than the main command line of your computer.

Next, you will be asked to determine how Git treats line endings in text files.  Git Setup recommends a setting based on your machine, it is easiest to follow that recommendation, especially if you are collaborating with different machines.

![Configuring the line ending conversions][screenshot2]

[screenshot2]: gitconversion.jpg

### How to clone a repository

Now that Git is installed, you can clone or create a repository.  To clone into a repository.  Git provides a [good guide][repository].

In order to clone into the repository for this project, for example, open Git Bash and run the following command:

	git clone https://github.com/neelsmith/latinsrcs

This checks out all the most current data in the repository.  Once the repository is cloned, it does not need to be cloned again.  To get the most current data, enter the repository by running a "change directory" command:

	cd latinsrcs

Then, update the material by pulling from the main repository.

	git pull

To make changes and add those changes to the repository, save your work and add any of the files you changed...

	git add [file name]

...then commit your changes.

	git commit

This command will open a text editor, where you must write a commit message describing the changes being made.  Then, push your changes into the repository.

	git push



[repository]: http://git-scm.com/book/en/Git-Basics-Getting-a-Git-Repository
