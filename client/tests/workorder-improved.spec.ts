import { test, expect } from '@playwright/test';

test.describe('工单详情页面样式测试', () => {
  test('测试改进后的工单详情页面', async ({ page }) => {
    console.log('开始测试：改进后的工单详情页面');
    
    await page.goto('http://localhost:8100/#/login');
    await page.waitForLoadState('networkidle');
    
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', '123456');
    await page.click('label:has-text("平台运营")');
    await page.click('button[type="button"]');
    await page.waitForTimeout(5000);
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(3000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    const detailButtons = page.locator('button:has-text("详情")');
    const buttonCount = await detailButtons.count();
    
    if (buttonCount > 0) {
      await detailButtons.first().click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/improved-workorder-detail.png' });
      
      const detailContainer = page.locator('.detail-container');
      expect(await detailContainer.isVisible()).toBeTruthy();
      
      const headerSection = page.locator('.detail-header');
      expect(await headerSection.isVisible()).toBeTruthy();
      
      const statusTags = page.locator('.detail-header .el-tag');
      const tagCount = await statusTags.count();
      console.log(`状态标签数量: ${tagCount}`);
      expect(tagCount).toBe(2);
      
      const sectionTitles = page.locator('.section-title');
      const titleCount = await sectionTitles.count();
      console.log(`区块标题数量: ${titleCount}`);
      expect(titleCount).toBeGreaterThan(0);
      
      const descriptions = page.locator('.el-descriptions');
      const descCount = await descriptions.count();
      console.log(`描述列表数量: ${descCount}`);
      expect(descCount).toBeGreaterThan(0);
      
      const closeButton = page.locator('button:has-text("关闭")');
      await closeButton.click();
      await page.waitForTimeout(1000);
    }
  });

  test('测试工单详情页面各个区块', async ({ page }) => {
    console.log('开始测试：工单详情页面各个区块');
    
    await page.goto('http://localhost:8100/#/login');
    await page.waitForLoadState('networkidle');
    
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', '123456');
    await page.click('label:has-text("平台运营")');
    await page.click('button[type="button"]');
    await page.waitForTimeout(5000);
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(3000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    const detailButtons = page.locator('button:has-text("详情")');
    const buttonCount = await detailButtons.count();
    
    if (buttonCount > 0) {
      await detailButtons.first().click();
      await page.waitForTimeout(2000);
      
      const basicInfoSection = page.locator('.detail-section').first();
      expect(await basicInfoSection.isVisible()).toBeTruthy();
      
      const sectionIcons = page.locator('.section-title i');
      const iconCount = await sectionIcons.count();
      console.log(`区块图标数量: ${iconCount}`);
      
      const imageBox = page.locator('.image-box');
      const hasImage = await imageBox.isVisible().catch(() => false);
      console.log(`是否有现场图片: ${hasImage}`);
      
      const resultContent = page.locator('.result-content');
      const resultCount = await resultContent.count();
      console.log(`结果内容区域数量: ${resultCount}`);
      
      await page.screenshot({ path: 'test-results/improved-workorder-sections.png' });
    }
  });
});
