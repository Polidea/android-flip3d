---

__This repository is no longer maintained. Issue reports and pull requests will not be attended.__

---

android-flip3d
==============

The widget provides capability of 3D flip- animated swap between two different views - back and front view. The views can be of arbitrary complexity.


The code was mostly copied as a starting point from Neil Davis' work here:  http://www.inter-fuser.com/2009/08/android-animations-3d-flip.html, assuming that the code by itself was put in public domain. 

We are releasing it under New BSD licence -  packaged and improved, with plans to add more features. You can see the flip3d animation here: (it was genereted and published by  Neil). 

[![android-coverflow](http://img.youtube.com/vi/a5b0EKUU3h4/0.jpg)](http://www.youtube.com/watch?v=a5b0EKUU3h4)

Few changes related to using views rather than images only have been implemented. Probably more to come.

In order to use the project, check it out, add it as library project to yours. You can also build it as  separate project (see README.txt for detail). Examples of usage of the widget can be found in main.xml file in layout:

[main.xml](https://github.com/Polidea/android-flip3d/blob/master/res/layout/main.xml)

Note! There is a proplem with minSdkLevel set to 8 - Flip3D shows some artifacts in this case for pictures while flipping.  This parameter should be removed from manifest to get it without artifacts. Kudos to Sebastian (tsiopani) fir finding it.
