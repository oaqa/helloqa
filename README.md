helloqa
=======

# Setup Instructions

The HelloQA pipeline works out of the box, but you'll need to download the
Gutenberg index input data separately (it's a big file and doesn't make sense to
keep it in the repository, bad practice and all).

## Dataset Preparation

!!! Note that the following is obsolete. Dataset is included in the repo.
See also https://github.com/oaqa/helloqa/wiki/DSO-Project !!!

1.  Download the Gutenberg index
[here](https://github.com/downloads/oaqa/helloqa/guten.tar.gz "guten.tar.gz").

2.  Untar it.

3.  Copy the resulting files into the data directory inside your HelloQA
project such that your helloqa/data/guten directory contains bin, conf, data,
lib, etc.

## Running the Pipeline (Eclipse)

You're now ready to run the pipeline.

1.  Open project in Eclipse.

2.  Expand the directory tree under the project and look for "launches".

3.  Expand "launches" and right-click on test.launch > Run As > test.  This
launch should now be the default behavior whenever you click the "Run" button
in the toolbar while you're in the helloqa project.
