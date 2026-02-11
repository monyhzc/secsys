import { test, expect } from '@playwright/test';

test.describe('角色管理功能测试', () => {
  test.beforeEach(async ({ page }) => {
    // 登录
    await page.goto('http://localhost:8100');
    await page.locator('input[type="text"]').fill('admin');
    await page.locator('input[type="password"]').fill('123456');
    await page.locator('button:has-text("登录")').click();
    await page.waitForTimeout(3000);
  });

  test('角色列表查询功能', async ({ page }) => {
    console.log('测试角色列表查询功能...');
    
    // 点击系统管理菜单
    const systemMenu = page.locator('text=系统管理').first();
    await systemMenu.click();
    await page.waitForTimeout(3000);
    
    // 点击角色管理菜单
    const roleMenu = page.locator('text=角色管理').first();
    await roleMenu.click();
    await page.waitForTimeout(5000);
    
    // 截图
    await page.screenshot({ path: 'test-results/role-list.png' });
    
    // 检查表格是否可见
    const table = page.locator('.el-table');
    expect(await table.isVisible()).toBeTruthy();
    
    // 检查表格列数
    const tableHeaders = page.locator('.el-table th');
    const headerCount = await tableHeaders.count();
    console.log(`表格列数: ${headerCount}`);
    expect(headerCount).toBeGreaterThan(0);
  });

  test('分配权限功能', async ({ page }) => {
    console.log('测试分配权限功能...');
    
    // 点击系统管理菜单
    const systemMenu = page.locator('text=系统管理').first();
    await systemMenu.click();
    await page.waitForTimeout(3000);
    
    // 点击角色管理菜单
    const roleMenu = page.locator('text=角色管理').first();
    await roleMenu.click();
    await page.waitForTimeout(5000);
    
    // 找到第一个角色的分配权限按钮
    const assignButtons = page.locator('button:has-text("分配权限")');
    const buttonCount = await assignButtons.count();
    console.log(`分配权限按钮数量: ${buttonCount}`);
    
    if (buttonCount > 0) {
      // 点击分配权限按钮
      await assignButtons.first().click();
      await page.waitForTimeout(3000);
      
      // 截图
      await page.screenshot({ path: 'test-results/role-assign.png' });
      
      // 检查权限树是否可见
      const tree = page.locator('.el-tree');
      expect(await tree.isVisible()).toBeTruthy();
      
      // 检查权限树节点
      const treeNodes = page.locator('.el-tree-node');
      const nodeCount = await treeNodes.count();
      console.log(`权限树节点数量: ${nodeCount}`);
      expect(nodeCount).toBeGreaterThan(0);
      
      // 关闭分配权限对话框
      const closeButton = page.locator('button:has-text("取消")');
      if (await closeButton.isVisible()) {
        await closeButton.click();
        await page.waitForTimeout(2000);
      }
    }
  });

  test.afterEach(async ({ page }) => {
    // 关闭浏览器
    await page.close();
  });
});
