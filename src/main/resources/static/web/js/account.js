Vue.createApp({
    data() {
        return {
            accountInfo: {
            },
            errorToats: null,
            errorMsg: null
        }
    },
    methods: {
        getData: function () {
            const urlParams = new URLSearchParams(window.location.search);
            const id = urlParams.get('id');
            axios.get(`/api/accounts/${id}`)
                .then((response) => {
                    //get client ifo
                    this.accountInfo = response.data;
                    this.accountInfo.transactions.sort((a, b) => (b.id - a.id));
                })
                .catch((error) => {
                    // handle error
                    this.errorMsg = "Error getting data";
                    this.errorToats.show();
                })
        },
        formatDate: function (date) {
            return new Date(date).toLocaleDateString('en-gb');
        },
        signOut: function () {
            axios.post('/api/logout')
                .then(response => window.location.href = "/web/index.html")
                .catch(() => {
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
          deleteAccount: function (id) {
                     const isConfirmed = confirm("Are you sure you want to delete the account?");
                      if (isConfirmed) {
                            axios.delete('/api/clients/current/accounts/' + id)
                                 .then(response => {
                                               window.location.href = "/web/accounts.html";
                                           })
                                           .catch(error => {
                                               this.errorMsg = error.response.data;
                                               this.errorToats.show();
                                           });
                      }
                },
    },
    mounted: function () {
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.getData();
    }
}).mount('#app')