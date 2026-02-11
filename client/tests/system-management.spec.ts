import { test, expect } from '@playwright/test';

test.describe('系统管理功能测试', () => {
  // 登录操作
  async function login(page: any) {
    await page.goto('http://localhost:8100');
    await page.fill('input[type="text"]', 'admin');
    await page.fill('input[type="password"]', '123456');
    await page.click('button[type="button"]');
    await page.waitForTimeout(3000);
  }

  test('员工管理功能测试', async ({ page }) => {
    console.log('测试员工管理功能...');
    
    // 登录
    await login(page);
    
    // 点击系统管理菜单
    const systemMenu = page.locator('text=系统管理').first();
    await systemMenu.click();
    await page.waitForTimeout(3000);
    
    // 点击员工管理菜单
    const userMenu = page.locator('text=员工管理').first();
    await userMenu.click();
    await page.waitForTimeout(5000);
    
    // 截图验证
    await page.screenshot({ path: 'test-results/system-user-list.png' });
    
    // 验证员工列表是否显示
    const table = page.locator('.el-table');
    expect(await table.isVisible()).toBeTruthy();
  });

  test('权限管理功能测试', async ({ page }) => {
    console.log('测试权限管理功能...');
    
    // 登录
    await login(page);
    
    // 点击系统管理菜单
    const systemMenu = page.locator('text=系统管理').first();
    await systemMenu.click();
    await page.waitForTimeout(3000);
    
    // 点击权限管理菜单
    const menuMenu = page.locator('text=权限管理').first();
    await menuMenu.click();
    await page.waitForTimeout(5000);
    
    // 截图验证
    await page.screenshot({ path: 'test-results/system-menu-list.png' });
    
    // 验证权限列表是否显示
    const table = page.locator('.el-table');
    expect(await table.isVisible()).toBeTruthy();
  });
});
