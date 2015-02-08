# Fits header keywords

There many many sources of fits keywords, many organisations (or groups of organisations) defined there own sets of keywords.
This results in many different dictionaries with partly overlapping definitions. To help the "normal" user of fits files
with these we started to collect the standards and will try to include them in this library to ease the use of the "right" 
keyword.

These dicrionaries are organized in a hirachical form, that means every ditionary extends the list of keywords of an other dictionary.
The root of this tree of dictionaries is the dictionary used in the standard text itself. Under that is a dictionary that resulted from
different libraries that used the same keywords, these are collected in a dicrionary with commonly used keywords.

These enumerations of keywords (dictionaries) can be found in and under the package [nom.tam.fits.header](./apidocs/nom/tam/fits/header/package-summary.html "nom.tam.fits.header").
The standard and commonly used keywords can be found there, where the commonly used keywords are sorted in separate enumerations by theme.
All included dictionaries of organisations can be found in the [nom.tam.fits.header.extra](./apidocs/nom/tam/fits/header/extra/package-summary.html "nom.tam.fits.header.extra") package.  

Currently we included:

* Standard
   source: [http://heasarc.gsfc.nasa.gov/docs/fcg/standard_dict.html](http://heasarc.gsfc.nasa.gov/docs/fcg/standard_dict.html) |
* Common standard
  inherits from Standard
  source: [http://heasarc.gsfc.nasa.gov/docs/fcg/common_dict.html](http://heasarc.gsfc.nasa.gov/docs/fcg/common_dict.html) |
* extra  standard
  inherits from Common standard
  source: we found these duplicated |
* NOAO
  inherits from extra  standard
  source: [http://iraf.noao.edu/iraf/web/projects/ccdmosaic/imagedef/fitsdic.html](http://iraf.noao.edu/iraf/web/projects/ccdmosaic/imagedef/fitsdic.html) |
* SBFits
  inherits from extra  standard
  source: [http://archive.sbig.com/pdffiles/SBFITSEXT_1r0.pdf](http://archive.sbig.com/pdffiles/SBFITSEXT_1r0.pdf) |
* MaxImDL
  inherits from SBFits
  source: [http://www.cyanogen.com/help/maximdl/FITS_File_Header_Definitions.htm](http://www.cyanogen.com/help/maximdl/FITS_File_Header_Definitions.htm) |
* CXCStclShared
  inherits from extra  standard
  source: we found these duplicated |
* CXC
  inherits from CXCStclShared
  source: [http://cxc.harvard.edu/contrib/arots/fits/content.txt](http://cxc.harvard.edu/contrib/arots/fits/content.txt) |
* STScI
  inherits from CXCStclShared
  source: [http://tucana.noao.edu/ADASS/adass_proc/adass_95/zaraten/zaraten.html](http://tucana.noao.edu/ADASS/adass_proc/adass_95/zaraten/zaraten.html) |


All duplicates where eliminated from enumerations including the enumerations that where already defined in one of the "parent" standards. So always use a keyword of one of the 
higher level standards when possible.

Furdermore there are synonym keywords inside and over dicrionaries, These we also started to collect in the Synonyms class in the header package. So you can easlly find the 
primary keyword to use in stead of the synonym. 

All these enums have no real effect for now in the library appart from the fact that you can now make compiler checked references to keywords (No more pruney String references).
Future versions of the library will try to validate against them and warn you if you use a "strange" constellation, or a depricated keyword.

These are the dictionaries we collected and tried to find the depricated/synonym/inherited headers and we need help, if you find an other dictionary or some missing/wrong keywords
please report it, any way is ok but the most appriciated would be a pull request. 