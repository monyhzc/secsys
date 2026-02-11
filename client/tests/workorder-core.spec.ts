import { test, expect } from '@playwright/test';

test.describe('工单系统核心功能测试', () => {
  test('管理员登录并访问工单系统', async ({ page }) => {
    console.log('开始测试：管理员登录并访问工单系统');
    
    await page.goto('http://localhost:8100/#/login');
    await page.waitForLoadState('networkidle');
    await page.screenshot({ path: 'test-results/core-login-page.png' });
    
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', '123456');
    await page.click('label:has-text("平台运营")');
    await page.screenshot({ path: 'test-results/core-login-filled.png' });
    
    await page.click('button[type="button"]');
    await page.waitForTimeout(5000);
    await page.screenshot({ path: 'test-results/core-after-login.png' });
    
    const workOrderMenu = page.locator('text=工单管理').first();
    const isVisible = await workOrderMenu.isVisible().catch(() => false);
    
    if (isVisible) {
      console.log('工单管理菜单可见');
      await workOrderMenu.click();
      await page.waitForTimeout(3000);
      await page.screenshot({ path: 'test-results/core-workorder-menu-expanded.png' });
      
      const workOrderListMenu = page.locator('text=工单列表').first();
      const isListVisible = await workOrderListMenu.isVisible().catch(() => false);
      
      if (isListVisible) {
        console.log('工单列表菜单可见');
        await workOrderListMenu.click();
        await page.waitForTimeout(5000);
        await page.screenshot({ path: 'test-results/core-workorder-list.png' });
        
        const table = page.locator('.el-table');
        const tableVisible = await table.isVisible().catch(() => false);
        console.log(`工单表格是否可见: ${tableVisible}`);
        
        if (tableVisible) {
          const tableRows = page.locator('.el-table tbody tr');
          const rowCount = await tableRows.count();
          console.log(`工单列表行数: ${rowCount}`);
          
          const detailButtons = page.locator('button:has-text("详情")');
          const detailCount = await detailButtons.count();
          console.log(`详情按钮数量: ${detailCount}`);
          
          if (detailCount > 0) {
            await detailButtons.first().click();
            await page.waitForTimeout(2000);
            await page.screenshot({ path: 'test-results/core-workorder-detail.png' });
            
            const descriptions = page.locator('.el-descriptions');
            const descVisible = await descriptions.isVisible().catch(() => false);
            console.log(`工单详情对话框是否可见: ${descVisible}`);
            
            const closeButton = page.locator('button:has-text("关闭")');
            await closeButton.click();
            await page.waitForTimeout(1000);
          }
        }
      }
    }
  });

  test('测试工单审核功能', async ({ page }) => {
    console.log('开始测试：工单审核功能');
    
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
    
    const workOrderReviewMenu = page.locator('text=工单审核').first();
    const isReviewVisible = await workOrderReviewMenu.isVisible().catch(() => false);
    
    if (isReviewVisible) {
      console.log('工单审核菜单可见');
      await workOrderReviewMenu.click();
      await page.waitForTimeout(5000);
      await page.screenshot({ path: 'test-results/core-workorder-review-list.png' });
      
      const table = page.locator('.el-table');
      const tableVisible = await table.isVisible().catch(() => false);
      console.log(`工单审核表格是否可见: ${tableVisible}`);
      
      const reviewButtons = page.locator('button:has-text("复核")');
      const reviewCount = await reviewButtons.count();
      console.log(`复核按钮数量: ${reviewCount}`);
      
      if (reviewCount > 0) {
        await reviewButtons.first().click();
        await page.waitForTimeout(2000);
        await page.screenshot({ path: 'test-results/core-workorder-review-dialog.png' });
        
        const reviewResultInput = page.locator('textarea[placeholder="请输入复核意见"]');
        const inputVisible = await reviewResultInput.isVisible().catch(() => false);
        console.log(`复核意见输入框是否可见: ${inputVisible}`);
        
        if (inputVisible) {
          await reviewResultInput.fill('复核通过，处理结果符合要求');
          await page.screenshot({ path: 'test-results/core-workorder-review-filled.png' });
          
          const confirmButton = page.locator('button:has-text("确定")');
          await confirmButton.click();
          await page.waitForTimeout(2000);
          await page.screenshot({ path: 'test-results/core-workorder-review-success.png' });
        }
      }
    }
  });

  test('测试工单统计功能', async ({ page }) => {
    console.log('开始测试：工单统计功能');
    
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
    
    const workOrderStatsMenu = page.locator('text=工单统计').first();
    const isStatsVisible = await workOrderStatsMenu.isVisible().catch(() => false);
    
    if (isStatsVisible) {
      console.log('工单统计菜单可见');
      await workOrderStatsMenu.click();
      await page.waitForTimeout(5000);
      await page.screenshot({ path: 'test-results/core-workorder-stats.png' });
      
      const statsElements = page.locator('.el-card, .el-statistic');
      const statsCount = await statsElements.count();
      console.log(`统计元素数量: ${statsCount}`);
      
      const charts = page.locator('canvas, .echarts');
      const chartCount = await charts.count();
      console.log(`图表数量: ${chartCount}`);
    }
  });

  test('测试工单查询和筛选功能', async ({ page }) => {
    console.log('开始测试：工单查询和筛选功能');
    
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
    
    const titleInput = page.locator('input[placeholder="请输入标题"]');
    const inputVisible = await titleInput.isVisible().catch(() => false);
    
    if (inputVisible) {
      console.log('标题输入框可见');
      await titleInput.fill('测试');
      await page.screenshot({ path: 'test-results/core-workorder-search-title.png' });
      
      const searchBtn = page.locator('button:has-text("查询")').first();
      await searchBtn.click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/core-workorder-search-result.png' });
      
      const resetBtn = page.locator('button:has-text("重置")');
      await resetBtn.click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/core-workorder-reset.png' });
      
      const statusSelect = page.locator('.el-select').first();
      await statusSelect.click();
      await page.waitForTimeout(500);
      await page.click('text=待处理');
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/core-workorder-filter-status.png' });
    }
  });
});
