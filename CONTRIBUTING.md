# Contributing to PowerSystemDatamodel
Welcome dear fellow of sophisticated power system modelling! :wave:
And thank you for considering your contribution to this project!
With this document we would like to give you some orientation on how you can contribute.

## Table of contents
* [Testing and reporting bugs](#testing-and-reporting-bugs)
* [Suggest extensions](#suggest-extensions)
* [Contributing code](#contributing-code)
* [Branching and handing in pull requests](#branching-and-handing-in-pull-requests)
* [General (software) design guidelines](#general-software-design-guidelines)
* [Testing](#testing)
* [Finalising your pull request](#finalising-your-pull-request)
* [For any doubts](#for-any-doubts)

## Testing and reporting bugs
We really appreciate your usage of this project.
Whenever you find a bug, it would be nice to check, if this isn't a feature to us. :wink:
You may find extensive information about the intended behaviour when you [Read the Docs](https://powersystemdatamodel.readthedocs.io/en/latest/) or within the [javadoc](https://ie3-institute.github.io/PowerSystemDataModel/javadoc/).
If you still think it's a bug, please raise an [issue](https://guides.github.com/features/issues/) for us.
Considering the following aspects in your inquiry, assists us in helping you:

* **Is there already an issue addressing your problem?**
* Try to **locate the error** as precise as possible.
* What has to be done to **reproduce the error**?
* **Provide stack trace, logs etc.** and further helpful information
* **What would do you expect to happen?**
* Mark the issue with the **label _bug_**.

## Suggest extensions
We use issues as well to keep track of enhancement suggestions.
Considering the following aspects, assists us in understanding your needs properly:

* **Is there already an issue addressing your request?**
* **What would do you desire for?**
* If possible provide an **example or sketch**.
* Show a **use case**, that should be as versatile as possible.
* Mark the issue with the **label _enhancement_**.

## Contributing code
If you intend to produce some lines of code, pick an issue and get some hands on!

### Branching and handing in pull requests
We try to follow a branch naming strategy of the form `<initials>/#<issueId>-<description>`.
If for example [Prof. Dr. rer. hort. Klaus-Dieter Brokkoli](https://www.instagram.com/prof_broccoli/) would like to add some work on node models reported in issue 4711, he would open a branch `kb/#4711-extendingNodeModels`.
Please hand in a _draft_ pull request as early as possible to allow other to keep track on your changes.
Before opening it for review, please [finalise your pull request](#finalising-your-pull-request).

### General (software) design guidelines
In order to maintain a consistent project, we thought of some general design guidlines, we kindly ask you to take care of:

* We :heart: **immutability**. Therefore, please don't provide setters and use proper instantiation instead.
* `double a = b * pow(x, j)`? :hand: Please **be expressive** in what you code!
* Document your code with **javadoc**.

### Testing
Ensure the proper function of your code by [test driven development (TDD)](https://www.guru99.com/test-driven-development.html).
We have good experiences using [Spock](http://spockframework.org/) as a testing framework for [Groovy](https://groovy-lang.org/).

### Finalising your pull request
Some automated checks assist us in delivering a pretty fair quality of software.
Before marking the pull request as 'ready to review', take these precautionary actions:

* Are all tests passing? Run `gradle test`
* Is your code properly formatted? Run `gradle spotlessApply`

`gradle finalizePR` summarizes all of these steps .

## For any doubts
... please contact
* Johannes (@johanneshiry),
* Debopama (@sensarmad) or
* Chris (@ckittl)

We are happy to help! :smiley:
