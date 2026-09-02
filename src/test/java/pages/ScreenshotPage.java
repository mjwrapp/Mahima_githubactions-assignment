package pages;

import com.microsoft.playwright.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotPage {

    private Playwright playwright;
    private Browser browser;
    private Page page;

    public void launchBrowser() {
        playwright = Playwright.create();

        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(true));

        page = browser.newPage();
    }

    public void navigateTo(String url) {
        page.navigate(url);
    }

    public Path takeScreenshot(String fileName) {

        Path path = Paths.get(fileName);

        page.screenshot(new Page.ScreenshotOptions()
                .setPath(path)
                .setFullPage(true));

        return path;
    }

    public void closeBrowser() {
        browser.close();
        playwright.close();
    }
}