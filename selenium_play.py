import os
import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.proxy import *
from selenium.webdriver.support import expected_conditions as EC

def get_webdriver():
	print("get_webdriver()")
	profile = webdriver.FirefoxProfile() 
	profile.set_preference("network.proxy.type", 1)
	profile.set_preference("network.proxy.http", "proxy.sis.saison.co.jp")
	profile.set_preference("network.proxy.http_port", 8080)
	profile.update_preferences() 
	return webdriver.Firefox(firefox_profile=profile)

def get_element_by_id(driver, id):
	print("get_element_by_id()", id)
	element = WebDriverWait(driver, 10).until(
		EC.presence_of_element_located((By.ID, id))
	)
	return element;

def get_element_by_name(driver, id):
	print("get_element_by_name()", id)
	element = WebDriverWait(driver, 10).until(
		EC.presence_of_element_located((By.NAME, id))
	)
	return element;

def get_element_by_xpath(driver, id):
	print("get_element_by_xpath()", id)
	element = WebDriverWait(driver, 10).until(
		EC.presence_of_element_located((By.XPATH, id))
	)
	return element;


###
###
###
def login(driver, username, password):
	print("login()", username, password)
	elem = get_element_by_id(driver, 'userid')
	elem.send_keys(username);

	elem = get_element_by_id(driver, 'password')
	elem.send_keys(password);

	submit = get_element_by_name(driver, 'submit')
	submit.click();

###
### ユーザ一覧から対戦相手を選ぶ
###
def click_userlist(driver, id):
	print("userlist()")
	elem = get_element_by_xpath(driver, id)
	elem.click();

###
### ダイアログに応答する
###
def click_dialog_comferme_ok(driver, id, ok):
	print("click_dialog_comferme_ok()")
	elem = get_element_by_id(driver, id)
	if elem.is_displayed():
		okbutton = get_element_by_xpath(driver, ok);
		okbutton.click();

def first_secound(d1, d2):
	elem = get_element_by_id(d1, "turnstatus")
	print("text driver ?", elem.text)
	if elem.text == "あなたの番です":
		drivers = [d1, d2]
	else:
		drivers = [d2, d1]
	return drivers
	
#
# 1  2  3
# 4  5  6
# 7  8  9
def play(drivers):
	list = [1, 2, 5, 3, 9]
	i = 0
	for grid in list:
		elem = get_element_by_id(drivers[i % 2], "grid_" + str(grid))
		elem.click()
		time.sleep(1)
		i = i + 1

driver1 = get_webdriver();
driver2 = get_webdriver();
driver1.get("http://localhost:8080");
driver2.get("http://localhost:8080");
login(driver1, "morita", "password");
login(driver2, "hoge", "hoge");

time.sleep(3)
click_userlist(driver1, "//button[contains(text(), \"待機中\")]")
time.sleep(3)
click_dialog_comferme_ok(driver2, "show_dialog", "//button[@type='button']/span[text()='OK']")

drivers = first_secound(driver1, driver2)
play(drivers)

click_dialog_comferme_ok(driver1, "show_dialog", "//button[@type='button']/span[text()='OK']")
click_dialog_comferme_ok(driver2, "show_dialog", "//button[@type='button']/span[text()='OK']")