package com.iplautomation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class IPLWebTabelAutomation {

	public static void main(String[] args) {

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--start-maximized");

		WebDriver wd = new ChromeDriver(chromeOptions);
		WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(60));

		wd.get("https://www.iplt20.com/points-table");

		By acceptCookies = By.xpath("//button[contains(.,'Accept All Cookies')]");
		By essentialOnly = By.xpath("//button[contains(.,'Essential Only')]");

		try {
			WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(acceptCookies));
			acceptButton.click();
		} catch (TimeoutException e) {
			try {
				WebElement essentialButton = wait.until(ExpectedConditions.elementToBeClickable(essentialOnly));
				essentialButton.click();
			} catch (TimeoutException e2) {
				System.out.println("Cookie banner not found on the page");
			}
		}

		// Step1 : Find the entire web table
		By tableLocator = By.xpath("//table[@data-testid='ipl-points-table-wide']");
		WebElement tableElement = wait.until(ExpectedConditions.visibilityOfElementLocated(tableLocator));

		// Step 2: Locate the tbody using chaining of WebElement
		WebElement tBodyElement = tableElement.findElement(By.tagName("tbody"));

		// Step 3: Find all table row
		By tableRow = By.tagName("tr");
		List<WebElement> tableRowList = tBodyElement.findElements(tableRow);

		List<Team> teamList = new ArrayList<>();
		for (WebElement row : tableRowList) {
			By tableDataRowLocator = By.cssSelector("td, th");
			List<WebElement> tableDataList = row.findElements(tableDataRowLocator);

			double nrr = Double.parseDouble(tableDataList.get(7).getText());
			int pts = Integer.parseInt(tableDataList.get(6).getText());
			String teamName = tableDataList.get(1).getText().replaceAll("\\s*\\n\\s*Qualified for playoffs.*", "")
					.trim();
			Team team = new Team(tableDataList.get(0).getText(), teamName, nrr, pts);
			teamList.add(team);
		}

		// max
		int maxPoints = teamList.stream().mapToInt(i -> i.getPoints()).max().orElse(0);
		System.out.println(maxPoints);

		teamList.stream().filter(i -> i.getPoints() == maxPoints).map(i -> i.getTeamName())
				.forEach(i -> System.out.println(i));

		// To print max NRR of same points team
		Team maxTeamNRR = teamList.stream().filter(i -> i.getPoints() == maxPoints)
				.max(Comparator.comparingDouble(i -> i.getNrr())).orElse(null);

		System.out.println(maxTeamNRR);

		wd.quit();

	}

}
