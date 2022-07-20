# Hippo External Document Picker 

This project provides a base for external document pickers for Hippo CMS with simple example implementations for 
developer's references.     

This project consists of two different external document pickers: 
1. one for External Document Field Picker 
2. one for External Link Picker Button in the Rich Text Editor. 

# Documentation (Local)

The documentation can generated locally by this command:

 > mvn clean site

The output is in the ```target/site/``` directory by default. You can open ```target/site/index.html``` in a browser.

# Documentation (GitHub Pages)

Documentation is available at [bloomreach-forge.github.io/external-document-picker](https://bloomreach-forge.github.io/external-document-picker).

You can generate the GitHub pages only from ```master``` branch by this command:

 > mvn -Pgithub.pages clean site

The output is in the ```docs/``` directory by default. You can open ```docs/index.html``` in a browser.

You can push it and GitHub Pages will be served for the site automatically.
