

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

Open the plugin settings and enter your API key.


<img src="../openAi_key_input.png" width="500"/>


- The API key is Base64-encoded and stored as an XML file under the `.idea/` directory.
- Make sure to **add `.idea/` to your `.gitignore`:**

```
.idea
```

### 3. Right-click in the editor and select **“Code Generation”**


<img src="../generation_btn.png" width="500"/>

---


### 4. More Configuration

#### 4.1 Mark References for User Prompt


When generating a domain API implementation, the user prompt must include the API source and related data specs to produce functioning code.

During plugin execution, the plugin collects the required classes for each task based on these annotations.



<img src="/docs/hackerNews/reference_ruls.png" width="500"/>

You can find the exact collection rules at the link below:

Link: [3. collect sources.md](3_collect_sources.md)


<br/>



#### 4.2 Define your repetitive tasks using natural language.

Since each developer has their own preferences and priorities, the definition of rules must be customizable.

For example, I prefer to keep tests in the infra layer as close to pure Java as possible. However, someone else might find such a rule excessive. Therefore, customization of these rules must be supported.


<img src="/docs/hackerNews/custom_patterns.png" width="500"/>

You can find the exact specs of pattern management and `how-to` video 

Link: [2. Define your pattern](2_define_your_pattern.md)


<br/>


#### 4.3 set patternName in your interface and run


<img src="/docs/images/run_patterns.png" width="500"/>


once you set pattens and annotation, set `@JavaFactoryPattern( value = "pattern_name" )` or `@JavaFactoryPattern( value = ["pattern_name1","pattern_name2" ] )`.
right-click your file and run generation.
