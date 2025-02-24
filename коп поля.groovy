import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException
import groovy.json.JsonSlurper
 
// Проверка, принадлежит ли задача проекту SSATEST
if (issue.getProjectObject().getKey() != "SSATEST") {
return true // Выход из скрипта, если это не проект SSATEST
}
 
//Проверяет валидность Grid поляЗапрос VPN (Jira/wiki),столбец Мобильный номер
// Функция для поиска номеров телефонов в сложной структуре данных
def findPhoneNumbers(data, phoneRegex, errors) {
if (data instanceof Map) {
data.each { key, value ->
if (key == 'Mobile') {
validatePhoneNumber(value, phoneRegex, errors)
} else {
findPhoneNumbers(value, phoneRegex, errors)
}
}
} else if (data instanceof Collection) {
data.each { item ->
findPhoneNumbers(item, phoneRegex, errors)
}
}
}
 
// Функция для валидации номера телефона
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
 
// Основной код валидатора
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
CustomField gridField = customFieldManager.getCustomFieldObject(13529)
 
def gridValue = issue.getCustomFieldValue(gridField)
 
// Регулярное выражение для проверки формата телефона
def phoneRegex = /^(+77)(\d{10})$/
 
// Переменная для хранения ошибок
<ac:structured-macro ac:name="unmigrated-wiki-markup" ac:schema-version="1" ac:macro-id="8b29a25b-5b59-46e3-ae4b-bfe995adbaba"><ac:plain-text-body><![CDATA[def errors = [] as Set // Используем Set для исключения дубликатов
]]></ac:plain-text-body></ac:structured-macro>
 
// Обработка gridValue
if (gridValue instanceof String) {
try {
def jsonSlurper = new JsonSlurper()
gridValue = jsonSlurper.parseText(gridValue)
} catch (Exception e) {
errors << "Ошибка при обработке данных: ${e.message}"
}
}
 
findPhoneNumbers(gridValue, phoneRegex, errors)
 
if (errors) {
def errorMessage = "Обнаружены следующие ошибки:\n" + errors.join("\n")
throw new InvalidInputException(errorMessage)
}
 
return true