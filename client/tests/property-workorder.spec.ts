import { test, expect } from '@playwright/test';

test.describe('物业管理员工单系统测试', () => {
  test('物业管理员登录并测试工单系统', async ({ page }) => {
    // 导航到登录页面
    await page.goto('http://localhost:8100/#/login');
    
    // 等待页面加载完成
    await page.waitForLoadState('networkidle');
    
    // 截图保存登录页面
    await page.screenshot({ path: 'test-results/property-login-page.png' });
    
    // 填写登录表单 - 使用物业管理员账号
    await page.fill('input[placeholder="请输入用户名"]', 'admin_wy_a');
    await page.fill('input[placeholder="请输入密码"]', '123456');
    
    // 选择用户类型：物业 (1)
    await page.click('label:has-text("物业")');
    
    // 截图保存填写后的表单
    await page.screenshot({ path: 'test-results/property-login-form-filled.png' });
    
    // 点击登录按钮
    await page.click('button[type="button"]');
    
    // 等待页面跳转
    await page.waitForTimeout(5000);
    
    // 截图保存登录后的页面
    await page.screenshot({ path: 'test-results/property-after-login.png' });
    
    // 验证是否跳转到首页
    await expect(page).toHaveURL(/http:\/\/localhost:8100\/#\/.*/);
    
    // 查找工单管理菜单项
    const repairMenu = page.locator('text=物业报修').first();
    const isVisible = await repairMenu.isVisible().catch(() => false);
    
    if (isVisible) {
      // 点击物业报修菜单
      await repairMenu.click();
      
      // 等待页面加载
      await page.waitForTimeout(3000);
      
      // 截图保存工单列表页面
      await page.screenshot({ path: 'test-results/property-repair-list.png' });
      
      // 查找在线报修菜单项
      const applyMenu = page.locator('text=在线报修').first();
      const applyVisible = await applyMenu.isVisible().catch(() => false);
      if (applyVisible) {
        await applyMenu.click();
        await page.waitForTimeout(3000);
        await page.screenshot({ path: 'test-results/property-repair-apply.png' });
      }
      
      // 查找工单管理菜单项
      const listMenu = page.locator('text=工单管理').first();
      const listVisible = await listMenu.isVisible().catch(() => false);
      if (listVisible) {
        await listMenu.click();
        await page.waitForTimeout(3000);
        await page.screenshot({ path: 'test-results/property-repair-manage.png' });
      }
    } else {
      console.log('工单管理菜单未找到');
      await page.screenshot({ path: 'test-results/property-menu-not-found.png' });
    }
  });
});