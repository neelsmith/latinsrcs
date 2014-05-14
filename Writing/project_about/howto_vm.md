# Installing a Virtual Machine

All the tools that are used in this project can be run on a virtual machine, which is located in a GitHub repository.  To run this project or create a similar project with different material, download and install the virtual machine.  This is a combination of two pieces of software and then the [project-specific configuration][cite] from GitHub.

[cite]: https://github.com/neelsmith/latinvm

First, install Virtual Box: [https://virtualbox.org/wiki/Downloads][download].

[download]: https://www.virtualbox.org/wiki/Downloads

Then, install Vagrant: [https://vagrantup.com/downloads.html][vagrant].

[vagrant]: http://www.vagrantup.com/downloads.html

In your Git Bash, run the following commands:

	install vagrant-vbguest
	git clone https://github.com/neelsmith/latinvm.git

This should bring up the virtual machine.  It may take a while to boot the machine for the first time, because your computer is downloading and building the whole VM.  After the first time, however, it will be easy to start up the machine on your computer.

### Starting up

Change directory into the cloned VM repository.

	cd latinvm
	
Then bring up the machine and connect to it:

	vagrant up
	vagrant ssh

### Shutting down

Unless you shut down the VM when you are finished, it will run in the background of your computer.  First exit the ssh and then shut down the machine with the following commands:

	exit
	vagrant halt
