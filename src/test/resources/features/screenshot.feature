Feature: Capture and Upload Screenshot

  Scenario: Take Google screenshot and upload to S3
    Given the browser is launched
    When I navigate to "https://www.google.com"
    And I capture a screenshot named "google.png"
    Then the screenshot should be uploaded to S3