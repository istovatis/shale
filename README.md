shale
=====

Shale is a semantic concept map web application developed in GWT. Concept mapping has been developed as
a learning activity method by which students develops, externalize and communicate cognitive schemata to others. The deployed project can be accessed in http://83.212.101.82:8080/webmaps6/ Non register users can access web application as guests with username:guest and password:guest.

Major features:
* Core issues identification with Girvan Newman algorithm (Newman, M. E., & Girvan, M. (2004). [Finding and evaluating community structure in networks] (http://www.soc.ucsb.edu/faculty/friedkin/Syllabi/Soc148/Newman%20Girvan%202003.pdf). Physical review E)
* Pairs of core issues similarity Calculation based on  [A Similarity Measure for Clustering and Its Applications] (https://www.cisuc.uc.pt/publication/show/1825) by Guadalupe J. Torres et al. algorithm.
* User defined core issues.
* Concept and linking phrases Drag And Drop functionality with [gwt-dnd] (https://code.google.com/p/gwt-dnd/)
* Linking phrases implementation with [gwt-links] (https://code.google.com/p/gwt-links/)
* Client-Server communication with [RPC] (http://www.gwtproject.org/doc/latest/tutorial/RPC.html)
* User management 
* Apache UIMA integration
* WordNet support
* Multilingual support with i18n library. (Currently English and Greek supported)
