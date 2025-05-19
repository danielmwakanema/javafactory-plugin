# JavaFactory Pattern Guide

## Table of Contents

- [1. Overview](#1-overview)
- [2. Structure of LLM Prompt](#2-structure-of-llm-prompt)
    - [Example](#example)
- [3. Pattern Creation Panel](#3-pattern-creation-panel)
    - [System Prompt Fields](#system-prompt-fields)
        - [Best Practices for System Prompt](#best-practices-for-system-prompt)
        - [Preview](#preview)
    - [User Prompt](#user-prompt)
        - [Example User Prompt](#example-user-prompt)

<br/>


## 1. Overview

A **pattern** in JavaFactory is a set of instructions that guides the LLM on how to generate code.  
It allows users to customize what kind of code should be created and how, depending on their environment and needs.

For example:
- If you want to automate test code generation, you can define a pattern for test creation.
- If your goal is to automate mapper class creation, you can define a pattern accordingly.

Patterns provide flexibility to define reusable and domain-specific automation logic.


👉 [Watch the pattern definition video, real example](https://www.youtube.com/watch?v=kqHGkCpoQz8)


---

<br/>

## 2. Structure of LLM Prompt

JavaFactory uses two parts to construct prompts sent to the LLM:

- **System Prompt**: Describes the task, goal, rules, and output format.
- **User Prompt**: Injects real-time user input (e.g., target interface, implementation, related classes).

This structure ensures modular, reusable instruction while enabling highly customized generation.

### Example

Here is a simplified example for users new to LLM-based workflows:

```
## Goal 
Your task is to write test code for a given domain-level API.

You will receive:
 - An API interface
 - The implementation class of the interface
 - Additional required classes like data models or utilities

## Rules

1. Write test cases for all methods defined in the interface.
2. Cover both success and failure scenarios.

## Output

A complete Java file as output.

## Example
...
```

```
<< API INTERFACE >>

...

<< API IMPLEMENTATION >>

...

<< OTHER CLASSES >>

...
```


<br/>

## 3. Pattern Creation Panel

You can define and edit patterns from the Pattern Creation panel.

Default template values are provided. You can adjust them to suit your automation goals.

<br/>

### System Prompt Fields

- **Goal**: Describe what the LLM should do (e.g., "Write unit tests for this interface")
- **Rules**: Guidelines or constraints to follow during generation.( e.g., "use junit5 for test")
- **output**: Expected output format
- **example**: Provide an illustrative snippet to show the desired style (**This field can significantly influence the generated result.**) 

#### Best Practices for System Prompt

1. Keep the `output` format consistent to maintain stable results.
2. Avoid overly detailed examples, which may cause biased completions. Instead, consider using commented templates like this:

```java
public class FooImpl {
    Foo read(Long id) {
        // ... read and return using public method of given classes ...
    }
}
```

#### Preview

You can preview how your system prompt will look before saving it.


<img src="../images/system1.png" width="500"/>

<img src="../images/system2.png" width="500"/>

<img src="../images/system_preview.png" width="500"/>

---

<br/>

### User Prompt

In the user prompt, actual source code required for the task is directly referenced and sent.
For example, if you want to include the implementation class and its associated data classes when generating test code,
you can configure the user prompt to include those classes.

If a class is annotated with a specific role, and that role matches the target defined in the pattern,
it will be automatically included.

#### Example User Prompt

To define the same user prompt as in the example above, use:

```
<< API INTERFACE >>

...

<< API IMPLEMENTATION >>

...

<< OTHER CLASSES >>

...
```


<img src="../images/user1.png" width="500"/>
<img src="../images/user_preview.png" width="500"/>

For more details on how the annotations work, please refer to the document below. : [collecting sources](3_collect_sources.md)