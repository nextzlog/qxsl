qxsl: Hamradio Logging Library & LISP
====

![image](https://img.shields.io/badge/Java-SE11-red.svg)
![image](https://img.shields.io/badge/Gradle-5-orange.svg)
![image](https://img.shields.io/badge/license-LGPL3-blue.svg)

qxsl is a Java Library for Logging & Scoring & Tabulation for Amateur-Radio Contests.
qxsl is a vital component of [Automatic Acceptance & Tabulation System (ATS)-4](https://github.com/nextzlog/ats4) for [ALLJA1 contest](http://ja1zlo.u-tokyo.org/allja1).

## Features

- qxsl provides log en/decoders for QXML, [ADIF(ADI/ADX)](http://adif.org), [Cabrillo](https://wwrof.org/cabrillo/), etc.
- qxsl provides tabulation & scoring frameworks for contests and awards.
- qxsl provides a LISP engine named *Elva*, and contest rules can be described in modern S-expression styles.

## Sample Codes

Because we are [Scalalians](https://www.scala-lang.org/), please be patient to read Scala codes!

### Document Model

The package `qxsl.model` defines the structure of log files, where each communication is handled as an `Item` object, while the entire log is represented as `List[Item]`.
Each `Item` contains some `Field` objects, which indicate properties such as `Time`, `Mode` and `Band`.
In addition, each `Item` holds two `Exch` objects, namely `Rcvd` and `Sent`, which involve some messages (`Field`s) exchanged by the operator and the contacted station.

```Scala
import qxsl.model.Item
val item = new Item
val rcvd = item.getRcvd
val sent = item.getSent
```

### Field Management

The package `qxsl.field` provides a management framework for `Field` implementations.
The class `FieldFormats` detects `FieldFormat` implementations [from the class path automatically](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html), and each `FieldFormat` provides en/decoders for individual `Field` implementation.

```Scala
val formats = new qxsl.field.FieldFormats
item.add(formats.cache(new QName("qxsl.org", "mode")).field("CW"))
item.add(formats.cache(new QName("adif.org", "MODE")).field("CW"))
```

This mechanism is utilized for en/decoding the *QXML* format, which is an alternative log format proposed by the qxsl development team.
*QXML* is extensible, and supports namespaces which have been prohibited in the traditional ADIF:

```XML
<?xml version="1.0" ?>
<list xmlns:qxsl="qxsl.org">
  <item qxsl:time="2017-06-03T16:17:00Z" qxsl:call="QV1DOK" qxsl:band="14000" qxsl:mode="CW">
    <rcvd qxsl:rstq="599" qxsl:code="120103"/>
    <sent qxsl:rstq="599" qxsl:code="100110"/>
  </item>
  <item qxsl:time="2017-06-04T00:01:00Z" qxsl:call="QD1QXB" qxsl:band="21000" qxsl:mode="CW">
    <rcvd qxsl:rstq="599" qxsl:code="110117"/>
    <sent qxsl:rstq="599" qxsl:code="100110"/>
  </item>
</list>
```

### Decoding & Encoding

The package `qxsl.table` provides a basic framework for en/decoding log files including QXML and ADIF.
The class `TableFormats` detects individual formats (`TableFormat`s) from the class path automatically, and also provides the `detect` method for automatic format detection.

```Scala
val formats = new qxsl.table.TableFormats()
val table: List[Item] = formats.decode(Files.newInputStream(path))
formats.forName("qxml").encoder(Files.newOutputStream(path)).encode(table)
```

### Unpacking Summary Sheets

The package `qxsl.sheet` provides an en/decoding framework similar to the `qxsl.table` package, except that `qxsl.sheet` handles contest summary sheets such as Cabrillo and [JARL summary sheet](https://www.jarl.org/Japanese/1_Tanoshimo/1-1_Contest/e-log.htm) R2.0.
The class `SheetFormats` manages individual `SheetFormat` implementations, and also provides the `unpack` method useful for extracting `List[Item]` from a summary sheet.

```Scala
val formats = new qxsl.sheet.SheetFormats()
val table: List[Item] = formats.unpack(Files.newBufferedReader(path))
```

### Scoring for Awards & Contests

The package `qxsl.ruler` provides a rulemaking framework for amateur radio awards and contests.
Each contest is represented as a `Contest` object, which involves multiple `Section` objects.
The `Section` object accepts `List[Item]` and validates the communications one by one, by invoking the `summarize` method.
The class `RuleKit` provides a LISP engine optimized for this process.

```Scala
import qxsl.ruler.{Contest,RuleKit,Section,Summary}

val contest: Contest = new RuleKit().eval("""
(contest "CQ AWESOME CONTEST"
  (section "CW 14MHz" (lambda it (verify it (list CW? 14MHz?))))
  (section "CW 21MHz" (lambda it (verify it (list CW? 21MHz?))))
  (section "CW 28MHz" (lambda it (verify it (list CW? 28MHz?))))
  (section "PH 14MHz" (lambda it (verify it (list PH? 14MHz?))))
  (section "PH 21MHz" (lambda it (verify it (list PH? 21MHz?))))
  (section "PH 28MHz" (lambda it (verify it (list PH? 28MHz?)))))""")

val section: Section = contest.getSection("CW 14MHz")
val summary: Summary = section.summarize(table)
summary.accepted.asScala.foreach(println)
summary.rejected.asScala.foreach(println)
println(summary.score)
println(summary.mults)
println(summary.total)
```

The original LISP engine is provided by the package `elva`.
qxsl contains [the definition of ALLJA1 contest](src/main/resources/qxsl/ruler/allja1.lisp) as a sample definition inside the JAR file.

## Documents

- [Javadoc](https://pafelog.net/qxsl/index.html)
- [History and Usage of ATS-4](https://pafelog.net/ats4.pdf)

## Build

[Gradle](https://gradle.org/) retrieves dependent libraries, runs test cases, and builds a JAR file automatically.

`$ gradle build`

## Contribution

Feel free to contact [@nextzlog](https://twitter.com/nextzlog) on Twitter.

## License

### Author

[無線部開発班](https://pafelog.net)

### Clauses

- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License(LGPL) as published by the Free Software Foundation (FSF), either version 3 of the License, or (at your option) any later version.

- This program is distributed in the hope that it will be useful, but **without any warranty**; without even the implied warranty of **merchantability or fitness for a particular purpose**.
See the GNU Lesser General Public License for more details.

- You should have received a copy of the GNU General Public License and GNU Lesser General Public License along with this program.
If not, see <http://www.gnu.org/licenses/>.
