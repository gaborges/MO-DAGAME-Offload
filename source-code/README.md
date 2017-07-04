# MO-DAGAME

MO-DAGAME is a multi-objective version of DAGAME, an evolutionary algorithm
for generating nearly-optimal feature models configurations.

MO-DAGAME is implemented on top of an slightly modified version of the
[jMetal framework](http://jmetal.sourceforge.net/), extending some of the most
popular multi-objective evolutionary algorithms it provides.

## Feature Models

A Java implementation of multi-objective feature models is provided, as well
as an SXFM parser to load feature models specified in SXFM format.

## Benchmark application

An Android application is also included which executes MO-DAGAME over a folder
of feature models previously copied to the phone storage.

A complete set of statistics, including quality indicators and the execution
time, is provided as a result of its execution.

## Requirements

- Android Studio 0.8 or later
- Java JDK
- Device supporting at least Android API 16 (for the benchmark application)

## Usage

In order to use the benchmark app:

- Create a folder in the device sdcard
(e.g. /sdcard/modagame-benchmark/models) and copy the input models in that
folder.
- Create a folder to store the results of the benchmark
(e.g. /sdcard/modagame-benchmark/output)
- Launch MO-DAGAME Benchmark and configure the path of both folders in the
Settings menu.
- Load the database
- Run the benchmark

## License

MO-DAGAME is free software and is distributed under the GPLv3 license.
See COPYING for more information.

## External code

This project uses code from third-parties, licensed under their own terms:
- [jMetal](http://jmetal.sourceforge.net/) by J.J. Durillo, A.J. Nebro and
E. Alba. Licensed under LGPL version 2.1
- [XSRandom](https://mobile.aau.at/~welmenre/res/XSRandom.java) by
Wilfried Elmenreich. Licensed under LGPL Version 3.
- [HPPC](http://labs.carrotsearch.com/hppc.html) by Carrot Search Labs.
Licensed under the Apache 2.0 License.
- [opencsv](http://opencsv.sourceforge.net/) by Glen Smith.
Licensed under the Apache 2.0 License.
- [Card Library](https://github.com/gabrielemariotti/cardslib) by
Gabriele Mariotti. Licensed under the Apache 2.0 License.
- [NumberPicker](https://github.com/michaelnovakjr/numberpicker)
by Michael Novak. Licensed under the Apache 2.0 License.
- [MO-DAGAME](http://caosd.lcc.uma.es/projects/famware/tools.htm) Licensed under the GNU GPLv3 license. More details:
G. G. Pascual, R. E. Lopez-Herrejon, M. Pinto, L. Fuentes, and A. Egyed, 
“Applying multiobjective evolutionary algorithms to dynamic software product lines for reconfiguring mobile applications,” 
Journal of Systems and Software, vol. 103, pp. 392–411, 2015.



