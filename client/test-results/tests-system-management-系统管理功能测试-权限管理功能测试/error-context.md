# Page snapshot

```yaml
- generic [ref=e1]:
  - img
  - generic [ref=e5]:
    - generic [ref=e6]: 物 业 管 理 系 统
    - generic [ref=e7]:
      - textbox "请输入用户名" [ref=e11]: admin
      - textbox "请输入密码" [ref=e15]: "123456"
      - generic [ref=e17]:
        - radiogroup [ref=e18]:
          - generic [ref=e19]:
            - radio "业主" [ref=e21] [cursor=pointer]:
              - radio [ref=e24]
              - generic [ref=e25]: 业主
            - radio "物业" [ref=e27] [cursor=pointer]:
              - radio [ref=e30]
              - generic [ref=e31]: 物业
            - radio "平台运营" [ref=e33] [cursor=pointer]:
              - radio [ref=e36]
              - generic [ref=e37]: 平台运营
        - generic [ref=e38]: 请选择用户类型
      - button "登录" [active] [ref=e41] [cursor=pointer]
```