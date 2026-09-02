Feature: Capture and Upload Screenshot

  Scenario: Take Google screenshot and upload to S3
    Given Browser is launched
    When navigate to "https://www.google.com"
    And capture a screenshot named "google.png"
    Then the screenshot should be uploaded to S3