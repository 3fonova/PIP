import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException
import groovy.json.JsonSlurper
 
// Check if the issue belongs to the SSATEST project
if (issue.getProjectObject().getKey() != "SSATEST") {
return true // Exit the script if it's not the SSATEST project
}
 
// Function to find and validate surname and phone numbers in a complex data structure
def validateFields(data, surnameRegex, phoneRegex, errors) {
if (data instanceof Map) {
data.each { key, value ->
if (key == 'Surname') {
validateSurname(value, surnameRegex, errors)
} else if (key == 'Mobile') {
validatePhoneNumber(value, phoneRegex, errors)
} else {
validateFields(value, surnameRegex, phoneRegex, errors)
}
}
} else if (data instanceof Collection) {
data.each { item ->
validateFields(item, surnameRegex, phoneRegex, errors)
}
}
}
 
// Function to validate surname
def validateSurname(surname, surnameRegex, errors) {
if (surname) {
if (!(surname ==~ surnameRegex)) {
errors << "Фамилия '$surname' имеет неверный формат. Фамилия должна быть написана с заглавной буквы кириллицей, возможно использование дефиса для двойных фамилий."
}
} else {
errors << "Фамилия не может быть пустой"
}
}
 
// Function to validate phone number
def validatePhoneNumber(phoneNumber, phoneRegex, errors) {
if (phoneNumber) {
phoneNumber = phoneNumber.toString().replaceAll("
s", "")
if (!(phoneNumber ==~ phoneRegex)) {
errors << "Номер телефона '$phoneNumber' имеет неверный формат. Ожидаемый формат: 7XXXXXXXXXX или +7XXXXXXXXXX"
}
} else {
errors << "Номер телефона не может быть пустым"
}
}
 
// Main validator code
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
CustomField gridField = customFieldManager.getCustomFieldObject(13529)
 
def gridValue = issue.getCustomFieldValue(gridField)
 
// Regular expression for surname format
<ac:structured-macro ac:name="unmigrated-wiki-markup" ac:schema-version="1" ac:macro-id="076cf2c7-118f-4a13-b4ff-cff1a12a4daa"><ac:plain-text-body><![CDATA[def surnameRegex = /^[А-ЯЁ][а-яё](-[А-ЯЁ][а-яё])?$/
]]></ac:plain-text-body></ac:structured-macro>
 
// Regular expression for phone number format
def phoneRegex = /^(+77)(\d{10})$/
 
// Variable to store errors
<ac:structured-macro ac:name="unmigrated-wiki-markup" ac:schema-version="1" ac:macro-id="43e4efdd-eafe-4008-8ad1-ca3970e52e2e"><ac:plain-text-body><![CDATA[def errors = [] as Set // Using Set to exclude duplicates
]]></ac:plain-text-body></ac:structured-macro>
 
// Processing gridValue
if (gridValue instanceof String) {
try {
def jsonSlurper = new JsonSlurper()
gridValue = jsonSlurper.parseText(gridValue)
} catch (Exception e) {
errors << "Ошибка при обработке данных: ${e.message}"
}
}
 
validateFields(gridValue, surnameRegex, phoneRegex, errors)
 
if (errors) {
def errorMessage = "Обнаружены следующие ошибки:\n" + errors.join("\n")
throw new InvalidInputException(errorMessage)
}
 
return true