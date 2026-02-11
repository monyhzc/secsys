import { test, expect } from '@playwright/test';

test.describe('工单系统详细功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:8100/#/login');
    await page.waitForLoadState('networkidle');
    
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', '123456');
    await page.click('label:has-text("平台运营")');
    await page.click('button[type="button"]');
    await page.waitForTimeout(5000);
  });

  test('工单列表查询和筛选功能', async ({ page }) => {
    console.log('测试工单列表查询和筛选功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(3000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    await page.screenshot({ path: 'test-results/detailed-workorder-list.png' });
    
    const table = page.locator('.el-table');
    expect(await table.isVisible()).toBeTruthy();
    
    const titleInput = page.locator('input[placeholder="请输入标题"]');
    if (await titleInput.isVisible()) {
      await titleInput.fill('测试');
      await page.screenshot({ path: 'test-results/detailed-workorder-search.png' });
      
      const searchBtn = page.locator('button:has-text("查询")').first();
      await searchBtn.click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/detailed-workorder-search-result.png' });
    }
    
    const resetBtn = page.locator('button:has-text("重置")');
    await resetBtn.click();
    await page.waitForTimeout(2000);
    await page.screenshot({ path: 'test-results/detailed-workorder-reset.png' });
  });

  test('工单详情查看功能', async ({ page }) => {
    console.log('测试工单详情查看功能...');
    
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
      await page.screenshot({ path: 'test-results/detailed-workorder-detail.png' });
      
      const descriptions = page.locator('.el-descriptions');
      expect(await descriptions.isVisible()).toBeTruthy();
      
      const closeButton = page.locator('button:has-text("关闭")');
      await closeButton.click();
      await page.waitForTimeout(1000);
    }
  });

  test('工单处理功能', async ({ page }) => {
    console.log('测试工单处理功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(3000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    const handleButtons = page.locator('button:has-text("处理")');
    const buttonCount = await handleButtons.count();
    
    if (buttonCount > 0) {
      await handleButtons.first().click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/detailed-workorder-handle-dialog.png' });
      
      const resultInput = page.locator('textarea[placeholder="请输入处理结果"]');
      if (await resultInput.isVisible()) {
        await resultInput.fill('已安排人员处理，预计今天完成');
        await page.screenshot({ path: 'test-results/detailed-workorder-handle-filled.png' });
        
        const confirmButton = page.locator('button:has-text("确定")');
        await confirmButton.click();
        await page.waitForTimeout(2000);
        await page.screenshot({ path: 'test-results/detailed-workorder-handle-success.png' });
      }
    }
  });

  test('工单审核功能', async ({ page }) => {
    console.log('测试工单审核功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(3000);
    
    const workOrderReviewMenu = page.locator('text=工单复核').first();
    await workOrderReviewMenu.click();
    await page.waitForTimeout(5000);
    
    await page.screenshot({ path: 'test-results/detailed-workorder-review-list.png' });
    
    const reviewButtons = page.locator('button:has-text("复核")');
    const buttonCount = await reviewButtons.count();
    
    if (buttonCount > 0) {
      await reviewButtons.first().click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/detailed-workorder-review-dialog.png' });
      
      const reviewResultInput = page.locator('textarea[placeholder="请输入复核意见"]');
      if (await reviewResultInput.isVisible()) {
        await reviewResultInput.fill('复核通过，处理结果符合要求');
        await page.screenshot({ path: 'test-results/detailed-workorder-review-filled.png' });
        
        const confirmButton = page.locator('button:has-text("确定")');
        await confirmButton.click();
        await page.waitForTimeout(2000);
        await page.screenshot({ path: 'test-results/detailed-workorder-review-success.png' });
      }
    }
  });

  test('工单统计功能', async ({ page }) => {
    console.log('测试工单统计功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(3000);
    
    const workOrderStatsMenu = page.locator('text=工单统计').first();
    await workOrderStatsMenu.click();
    await page.waitForTimeout(5000);
    
    await page.screenshot({ path: 'test-results/detailed-workorder-stats.png' });
    
    const statsElements = page.locator('.el-card, .el-statistic');
    const statsCount = await statsElements.count();
    console.log(`找到 ${statsCount} 个统计元素`);
    
    const charts = page.locator('canvas, .echarts');
    const chartCount = await charts.count();
    console.log(`找到 ${chartCount} 个图表`);
  });

  test('分页功能测试', async ({ page }) => {
    console.log('测试分页功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(3000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    await page.screenshot({ path: 'test-results/detailed-workorder-pagination.png' });
    
    const pagination = page.locator('.el-pagination');
    expect(await pagination.isVisible()).toBeTruthy();
    
    const pageSizeSelect = page.locator('.el-pagination__sizes .el-select');
    if (await pageSizeSelect.isVisible()) {
      await pageSizeSelect.click();
      await page.waitForTimeout(500);
      await page.click('text=20');
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/detailed-workorder-pagesize-20.png' });
    }
  });

  test('状态筛选功能测试', async ({ page }) => {
    console.log('测试状态筛选功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(3000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    const statusSelect = page.locator('.el-select').first();
    await statusSelect.click();
    await page.waitForTimeout(500);
    await page.click('text=待处理');
    await page.waitForTimeout(2000);
    await page.screenshot({ path: 'test-results/detailed-workorder-status-pending.png' });
    
    const searchBtn = page.locator('button:has-text("查询")').first();
    await searchBtn.click();
    await page.waitForTimeout(2000);
    await page.screenshot({ path: 'test-results/detailed-workorder-status-pending-result.png' });
  });

  test('表格显示和状态标签测试', async ({ page }) => {
    console.log('测试表格显示和状态标签...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(3000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(8000);
    
    await page.screenshot({ path: 'test-results/detailed-workorder-table.png' });
    
    const table = page.locator('.el-table');
    expect(await table.isVisible()).toBeTruthy();
    
    const tableHeaders = page.locator('.el-table th');
    const headerCount = await tableHeaders.count();
    console.log(`表格列数: ${headerCount}`);
    expect(headerCount).toBeGreaterThan(0);
    
    const tableRows = page.locator('.el-table tbody tr');
    const rowCount = await tableRows.count();
    console.log(`表格行数: ${rowCount}`);
    
    const statusTags = page.locator('.el-tag');
    const tagCount = await statusTags.count();
    console.log(`状态标签数: ${tagCount}`);
    
    if (rowCount > 0) {
      expect(tagCount).toBeGreaterThan(0);
    } else {
      console.log('表格中没有数据，跳过状态标签检查');
    }
  });
});
