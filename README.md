# JavaFactory IntelliJ Plugin

**JavaFactory** is an IntelliJ plugin that automatically generates repetitive code in Java/Kotlin projects.  
It uses LLM-based automation, enabling users to define patterns and generate code accordingly.

This plugin supports the following features:

1. Registration and management of code generation patterns
2. Collection of reference code to be included in prompts (based on `@JavaFactory` annotations)

---

## Demo

### Example: Generating Implementation & Test Code
![image](docs/example_gif1.gif)

### Example: Generating Implementation Code Only
![image](docs/example_gif2.gif)

JavaFactory automates the following tasks:

- Implementation of simple domain classes
- Implementation of test classes
- Auxiliary code generation including mappers and utility classes

All generation logic is fully customizable based on the user's defined patterns.

---

## How to Use

### 1. Add the Annotation Dependency

**Maven:**
```xml
<dependency>
    <groupId>io.github.javafactoryplugindev</groupId>
    <artifactId>javafactory-annotation</artifactId>
    <version>0.1.1</version>
</dependency>
```

**Gradle:**
```groovy
implementation 'io.github.javafactoryplugindev:javafactory-annotation:0.1.1'
```

### 2. Register Your OpenAI API Key

Go to the plugin settings menu and register your API key.

![image](docs/openAi_key_input.png)

- The API key is Base64-encoded and stored in an XML file under the `.idea/` directory.
- Be sure to **add `.idea/` to your `.gitignore`**:

```
.idea
```



### 3. right click and  click "Code Generation"

![image](docs/generation_btn.png)

---


## Other details 

- [Link: Collecting Referenced Classes]()

- [Link: Managing Patterns]()