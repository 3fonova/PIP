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
// Validate surname first
if (data.containsKey('Surname')) {
<ac:structured-macro ac:name="unmigrated-wiki-markup" ac:schema-version="1" ac:macro-id="85f218a9-a780-42c0-8e42-9a1dc2eed116"><ac:plain-text-body><![CDATA[ validateSurname(data['Surname'], surnameRegex, errors)
]]></ac:plain-text-body></ac:structured-macro>
if (!errors.isEmpty()) {
return // Stop validation if there's an error with the surname
}
}

// Then validate phone number
if (data.containsKey('Mobile')) {
