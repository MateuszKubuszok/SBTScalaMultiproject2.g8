# SBT Scala multiproject template 2.0

![https://travis-ci.org/MateuszKubuszok/SBTScalaMultiproject2](https://travis-ci.org/MateuszKubuszok/SBTScalaMultiproject2.svg)

Template of SBT Scala with:

 * two modules independent of each other: `first` and `second`,
 * one `common` project - used by both `first` and `second`,
 * [Scalariform](https://github.com/scala-ide/scalariform) configuration,
 * [Scoverage](https://github.com/scoverage/sbt-scoverage) configuration,
 * [Scalastyle](http://www.scalastyle.org/) configuration,
 * predefined [sub]tasks: `it:test`, `fun:test`, `test` which run tests as
   `IntegrationTest`/`FunctionalTest`/`Test` respectively,
 * some additional plugins I finding useful: coursive, revolder, sbt-lock, sbt-git.

## Customization

Start with changing module namespace to your own both inside Scala files as well as in `project/Settings.scala`. Make
sure to update initial command if you want to run `console` task of SBT. Update organization name and version number.

Within `build.sbt` use existing modules as basis how to use small DSL for applying common settings:

 * `project.from("name")` will create module from `modules/name` directory and set its SBT name to `name`,
 * `setName("name")` and `setDescription("description")` can be used for settings up artifact name and description with
   fluent interface,
 * `setInitialCommand("command")` will set console starting point to `your.namespace.{command}`,
 * `configureModule` will apply all common settings from `Settings.scala`,
 * `configureUnitTests`/`configureFunctionalTests`/`configureIntegrationTests` will add `unit:test`/`functional:test`/
   `integration:test` task to your module,
 * `dependsOnProjects(projects)` will set up both compile and test dependency (so tests will succeed only if module's
    own and it's dependency's tests will pass, and in test code you could use some common code from your dependencies),
 * each of those commands will return project allowing normal `.settings(settings)` application. For individual settings
   one can also use `modules/name/build.sbt` individual per-module settings.

You should also take a closer look at `project/Settings.scala` for tweaking `scalacOptions`,
`ScalariformKeys.preferences` and `scalastyle-config.xml` for defining defaults best suiting your project.

Last but not least, edit common resolvers and dependencies within `project/Dependencies.scala`

After understanding how template works you are encourage to remove `activator.properties`, `tutorial/index.html` and
providing your own `README` and `LICENSE` files.

## Overriding defaults checks

If possible make defaults as strict as possible and just loosen them where absolutely needed:

 * coverage disabling:

   ```scala
   // $COVERAGE:OFF$ [reason]
   // not measured 
   // $COVERAGE:ON$
   ```
 * formatting disabling:

   ```scala
   // format: OFF
   // not formatted
   // format: ON
   ```
 * style check disabling:

   ```scala
   // scalastyle:off [rule id]
   // not checked
   // scalastyle:on
   ```

It can be used for e.g disabling measurement of automatically generated code, formatting that merges lines into
something exceeding character limit or allowing null where interacting with Java code.

## Running main classes:

```bash
sbt "project first" run // or
sbt first/run

sbt "project second" run // or
sbt second/run
```

## Tests

### Running all tests with coverage and style check:

```bash
sbt clean coverage test coverageReport coverageAggregate scalastyle
```

If you measure coverage you have to clean project otherwise it will not instrument properly. (To be precise coverage
cache should be clean if you want to have correct results - if you have just built project and haven't run any tests
with coverage enabled you don't have to clean anything).

### Selecting test suites

Running selected suite:

```bash
sbt common/test
sbt common/fun:test
sbt common/it:test
```
