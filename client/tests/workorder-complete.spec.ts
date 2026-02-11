import { test, expect } from '@playwright/test';

test.describe('工单系统完整功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:8100/#/login');
    await page.waitForLoadState('networkidle');
    
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', '123456');
    await page.click('label:has-text("平台运营")');
    await page.click('button[type="button"]');
    await page.waitForTimeout(5000);
  });

  test('测试工单列表查询功能', async ({ page }) => {
    console.log('开始测试工单列表查询功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    const isVisible = await workOrderMenu.isVisible().catch(() => false);
    
    if (isVisible) {
      await workOrderMenu.click();
      await page.waitForTimeout(2000);
      
      const workOrderListMenu = page.locator('text=工单列表').first();
      const isListVisible = await workOrderListMenu.isVisible().catch(() => false);
      
      if (isListVisible) {
        await workOrderListMenu.click();
        await page.waitForTimeout(5000);
        
        await page.screenshot({ path: 'test-results/workorder-list-initial.png' });
        
        const titleInput = page.locator('input[placeholder="请输入标题"]');
        if (await titleInput.isVisible()) {
          await titleInput.fill('测试工单');
          await page.screenshot({ path: 'test-results/workorder-search-title.png' });
          
          const searchBtn = page.locator('button:has-text("查询")').first();
          await searchBtn.click();
          await page.waitForTimeout(2000);
          await page.screenshot({ path: 'test-results/workorder-search-result.png' });
        }
        
        const statusSelect = page.locator('.el-select');
        if (await statusSelect.first().isVisible()) {
          await statusSelect.first().click();
          await page.waitForTimeout(500);
          await page.click('text=待处理');
          await page.waitForTimeout(2000);
          await page.screenshot({ path: 'test-results/workorder-filter-status.png' });
        }
        
        const resetBtn = page.locator('button:has-text("重置")');
        await resetBtn.click();
        await page.waitForTimeout(2000);
        await page.screenshot({ path: 'test-results/workorder-reset.png' });
      }
    }
  });

  test('测试工单详情查看功能', async ({ page }) => {
    console.log('开始测试工单详情查看功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(2000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    const detailButtons = page.locator('button:has-text("详情")');
    const buttonCount = await detailButtons.count();
    
    if (buttonCount > 0) {
      await detailButtons.first().click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/workorder-detail-dialog.png' });
      
      const descriptions = page.locator('.el-descriptions');
      if (await descriptions.isVisible()) {
        console.log('工单详情对话框显示正常');
      }
      
      const closeButton = page.locator('button:has-text("关闭")');
      await closeButton.click();
      await page.waitForTimeout(1000);
    }
  });

  test('测试工单处理功能', async ({ page }) => {
    console.log('开始测试工单处理功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(2000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    const handleButtons = page.locator('button:has-text("处理")');
    const buttonCount = await handleButtons.count();
    
    if (buttonCount > 0) {
      await handleButtons.first().click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/workorder-handle-dialog.png' });
      
      const resultInput = page.locator('textarea[placeholder="请输入处理结果"]');
      if (await resultInput.isVisible()) {
        await resultInput.fill('测试处理结果：已安排人员处理，预计今天完成');
        await page.screenshot({ path: 'test-results/workorder-handle-filled.png' });
        
        const confirmButton = page.locator('button:has-text("确定")');
        await confirmButton.click();
        await page.waitForTimeout(2000);
        await page.screenshot({ path: 'test-results/workorder-handle-success.png' });
      }
    }
  });

  test('测试工单复核功能', async ({ page }) => {
    console.log('开始测试工单复核功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(2000);
    
    const workOrderReviewMenu = page.locator('text=工单审核').first();
    const isReviewVisible = await workOrderReviewMenu.isVisible().catch(() => false);
    
    if (isReviewVisible) {
      await workOrderReviewMenu.click();
      await page.waitForTimeout(5000);
      await page.screenshot({ path: 'test-results/workorder-review-list.png' });
      
      const reviewButtons = page.locator('button:has-text("复核")');
      const buttonCount = await reviewButtons.count();
      
      if (buttonCount > 0) {
        await reviewButtons.first().click();
        await page.waitForTimeout(2000);
        await page.screenshot({ path: 'test-results/workorder-review-dialog.png' });
        
        const reviewResultInput = page.locator('textarea[placeholder="请输入复核意见"]');
        if (await reviewResultInput.isVisible()) {
          await reviewResultInput.fill('复核通过，处理结果符合要求');
          await page.screenshot({ path: 'test-results/workorder-review-filled.png' });
          
          const confirmButton = page.locator('button:has-text("确定")');
          await confirmButton.click();
          await page.waitForTimeout(2000);
          await page.screenshot({ path: 'test-results/workorder-review-success.png' });
        }
      }
    }
  });

  test('测试工单统计功能', async ({ page }) => {
    console.log('开始测试工单统计功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(2000);
    
    const workOrderStatsMenu = page.locator('text=工单统计').first();
    const isStatsVisible = await workOrderStatsMenu.isVisible().catch(() => false);
    
    if (isStatsVisible) {
      await workOrderStatsMenu.click();
      await page.waitForTimeout(5000);
      await page.screenshot({ path: 'test-results/workorder-stats.png' });
      
      const statsElements = page.locator('.el-card, .el-statistic');
      const statsCount = await statsElements.count();
      console.log(`找到 ${statsCount} 个统计元素`);
      
      const charts = page.locator('canvas, .echarts');
      const chartCount = await charts.count();
      console.log(`找到 ${chartCount} 个图表`);
    }
  });

  test('测试分页功能', async ({ page }) => {
    console.log('开始测试分页功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(2000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    await page.screenshot({ path: 'test-results/workorder-pagination-initial.png' });
    
    const nextPageButton = page.locator('.el-pagination button:has-text("下一页")');
    if (await nextPageButton.isVisible()) {
      await nextPageButton.click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/workorder-pagination-next.png' });
    }
    
    const pageSizeSelect = page.locator('.el-pagination__sizes .el-select');
    if (await pageSizeSelect.isVisible()) {
      await pageSizeSelect.click();
      await page.waitForTimeout(500);
      await page.click('text=20');
      await page.waitForTimeout(2000);
      await page.screenshot({ path: 'test-results/workorder-pagesize-20.png' });
    }
  });

  test('测试状态筛选功能', async ({ page }) => {
    console.log('开始测试状态筛选功能...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(2000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    const statuses = ['待处理', '处理中', '待复核', '已完成'];
    
    for (const status of statuses) {
      const statusSelect = page.locator('.el-select').first();
      await statusSelect.click();
      await page.waitForTimeout(500);
      await page.click(`text=${status}`);
      await page.waitForTimeout(2000);
      await page.screenshot({ path: `test-results/workorder-status-${status}.png` });
      
      const searchBtn = page.locator('button:has-text("查询")').first();
      await searchBtn.click();
      await page.waitForTimeout(2000);
    }
  });

  test('测试工单表格显示', async ({ page }) => {
    console.log('开始测试工单表格显示...');
    
    const workOrderMenu = page.locator('text=工单管理').first();
    await workOrderMenu.click();
    await page.waitForTimeout(2000);
    
    const workOrderListMenu = page.locator('text=工单列表').first();
    await workOrderListMenu.click();
    await page.waitForTimeout(5000);
    
    await page.screenshot({ path: 'test-results/workorder-table-display.png' });
    
    const table = page.locator('.el-table');
    expect(await table.isVisible()).toBeTruthy();
    
    const tableHeaders = page.locator('.el-table th');
    const headerCount = await tableHeaders.count();
    console.log(`表格列数: ${headerCount}`);
    
    const tableRows = page.locator('.el-table tbody tr');
    const rowCount = await tableRows.count();
    console.log(`表格行数: ${rowCount}`);
    
    const statusTags = page.locator('.el-tag');
    const tagCount = await statusTags.count();
    console.log(`状态标签数: ${tagCount}`);
  });
});
